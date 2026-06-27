package mk.wp.dataanswering.backend.web.controler;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.service.*;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final RegisteredUserService registeredUserService;
    private final AuthUtils authUtils;
    private final SubscriptionService subscriptionService;
    private final UserService userService;
    private final PromptService promptService;
    private final ChatServiceRegistry chatServiceRegistry;
    @Value("${saved.chats.limit}")
    private int savedChatsLimit;

    @PostMapping("/delete")
    public String deleteAccount() {
        RegisteredUser user = authUtils.getCurrentRegisteredUser();
        registeredUserService.softDelete(user.getUserId());
        return "redirect:/logout";
    }

    @GetMapping("/{userId}")
    public String getUser(@PathVariable Long userId, Model model) throws AccessDeniedException {

        // handle if unathorized

        RegisteredUser current = (RegisteredUser) userService.getCurrentUser();
        long currentId = current.getUserId();

        // handle if not equals!

        Subscription active = subscriptionService.getActiveSubscription(userId);
        model.addAttribute("active", active);

        List<Subscription> history = subscriptionService.getSubscriptionHistory(userId).stream()
                .filter(s -> s.getId() != active.getId())
                .sorted(Comparator.comparing(Subscription::getStartTs).reversed())
                .toList();
        model.addAttribute("history", history);

        int usedTokens = promptService.getUsedTokens(userId);
        model.addAttribute("usedTokens", usedTokens);

        int numberOfChats = chatServiceRegistry.getCorrectChatService().getChatsForCurrentUser().size();
        model.addAttribute("numberOfChats", numberOfChats);
        model.addAttribute("maxNumberOfChats", savedChatsLimit);

        ///  SIDEBAR
        model.addAttribute("userId", currentId);

        model.addAttribute("username", current.getUserFirstName());

        model.addAttribute("plan", active.getPlan().getPlanName());
        model.addAttribute("chats", chatServiceRegistry.getCorrectChatService().getChatsForCurrentUser());

        return "user-details";
    }
}