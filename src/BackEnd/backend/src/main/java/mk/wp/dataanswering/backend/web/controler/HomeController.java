package mk.wp.dataanswering.backend.web.controler;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.service.ExternalToolService;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;



@Controller
@RequestMapping(path={"/", "/home"})
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    // private final ChatServiceRegistry chatServiceRegistry;
    private final SubscriptionService subscriptionService;
    private final ExternalToolService externalToolService;
    private final ChatServiceRegistry chatServiceRegistry;
    private final AuthUtils authUtils;



    @GetMapping()
    public String getHomePage(@RequestParam(value="error", required=false) String error, Model model) {
        model.addAttribute("bodyContent", "home");
        model.addAttribute(
            "supportedFileTypes",  
            String.join(",", externalToolService.getSupportedFileTypes())
        );

        if(error!=null){
            model.addAttribute("error", error);
        }

        try{
            if (authUtils.isLoggedIn()){
                RegisteredUser registeredUser = authUtils.getCurrentRegisteredUser();

                model.addAttribute("username", registeredUser.getUserFirstName());

                Subscription sub = subscriptionService.getActiveSubscription(registeredUser.getUserId());
                model.addAttribute("plan", sub.getPlan().getPlanName());
                model.addAttribute("chats", chatServiceRegistry.getCorrectChatService().getChatsForCurrentUser());   
            }
        } catch (Exception e) {
            return "redirect:/logout";
        }

        return "master-template";
    }    
}
