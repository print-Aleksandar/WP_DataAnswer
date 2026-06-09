package mk.wp.dataanswering.backend.web.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;


@Controller
@RequestMapping(path={"/", "/home"})
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final ChatServiceRegistry chatServiceRegistry;
    private final SubscriptionService subscriptionService;


    @GetMapping()
    public String getHomePage(Model model) {
        model.addAttribute("bodyContent", "home");

        try{
            User currentUser = userService.getCurrentUser();
            if (currentUser instanceof RegisteredUser registeredUser){
                model.addAttribute("username", registeredUser.getUserFirstName());

                Subscription sub = subscriptionService.getActiveSubscription(registeredUser.getUserId());
                model.addAttribute("plan", sub.getPlan().getPlanName());
                            
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }

        return "master-template";
    }

    @GetMapping("/start-chat")
    public String startChat(Model model) {
        model.addAttribute("Id", userService.getCurrentUser().getUserId());
        chatServiceRegistry.getCorrectChatService().startNewChat();
        return "chatDUMMY";
    }
}
