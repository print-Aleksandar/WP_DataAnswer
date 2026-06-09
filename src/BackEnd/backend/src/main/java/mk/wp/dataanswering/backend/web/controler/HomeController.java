package mk.wp.dataanswering.backend.web.controler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.service.ExternalToolService;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping(path={"/", "/home"})
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final ChatServiceRegistry chatServiceRegistry;
    private final SubscriptionService subscriptionService;
    private final ExternalToolService externalToolService;


    @GetMapping()
    public String getHomePage(Model model) {
        model.addAttribute("bodyContent", "home");

        try{
            User currentUser = userService.getCurrentUser();
            if (currentUser instanceof RegisteredUser registeredUser){
                model.addAttribute("username", registeredUser.getUserFirstName());

                Subscription sub = subscriptionService.getActiveSubscription(registeredUser.getUserId());
                model.addAttribute("plan", sub.getPlan().getPlanName());
                model.addAttribute("chats", registeredUser.getChats());   
            }
        } catch (Exception e) {
            
        }

        return "master-template";
    }

    @GetMapping("/start-chat")
    public String startChat(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("Id", user.getUserId());
        model.addAttribute(
            "supportedFileTypes", 
        String.join(",", externalToolService.getSupportedFileTypes())
        );
        model.addAttribute("chats", List.of()); // TODO
        model.addAttribute("messages", List.of()); // TODO
        
        Chat chat = chatServiceRegistry.getCorrectChatService().startNewChat();
        model.addAttribute("chat", chat);


        return "chatDUMMY";
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadFile(@RequestBody MultipartFile file) {
        externalToolService.tryUploadToAll(
            userService.getCurrentUser().getUserId(), 
            -1l, 
            file
        );
        
        return ResponseEntity.ok().build();
    }
    
}
