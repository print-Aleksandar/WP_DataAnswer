package mk.wp.dataanswering.backend.web.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
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
            User currentUser = userService.getCurrentUser();
            if (currentUser instanceof RegisteredUser registeredUser){
                model.addAttribute("username", registeredUser.getUserFirstName());

                Subscription sub = subscriptionService.getActiveSubscription(registeredUser.getUserId());
                model.addAttribute("plan", sub.getPlan().getPlanName());
                model.addAttribute("chats", chatServiceRegistry.getCorrectChatService().getChatsForCurrentUser());   
            }
        } catch (Exception e) {
            
        }

        return "master-template";
    }

    // NE ZNAM ZASHTO E OVOJ TUJ STAVENO NE GU VIDU POENTU
    // @GetMapping("/start-chat")
    // public String startChat(Model model) {
    //     User user = userService.getCurrentUser();
    //     model.addAttribute("Id", user.getUserId());
    //     model.addAttribute(
    //         "supportedFileTypes", 
    //     String.join(",", externalToolService.getSupportedFileTypes())
    //     );
    //     model.addAttribute("chats", List.of()); // TODO
    //     model.addAttribute("messages", List.of()); // TODO
        
    //     Chat chat = chatServiceRegistry.getCorrectChatService().startNewChat();
    //     model.addAttribute("chat", chat);


    //     // return "chat";
    //     return "chatDUMMY";
    // }

    // @PostMapping("/upload")
    // public ResponseEntity<Void> uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("chatId") String chatId) {
    //     externalToolService.tryUploadToAll(
    //         userService.getCurrentUser().getUserId(), 
    //         Long.parseLong(chatId),
    //         file
    //     );
        
    //     return ResponseEntity.ok().build();
    // }
    
}
