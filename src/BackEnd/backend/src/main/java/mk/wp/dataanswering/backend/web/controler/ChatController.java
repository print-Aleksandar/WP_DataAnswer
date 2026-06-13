package mk.wp.dataanswering.backend.web.controler;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.service.PromptService;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.UploadFileService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;


@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatServiceRegistry chatServiceRegistry;
    private final UserService userService;
    private final PromptService promptService;
    private final SubscriptionService subscriptionService;
    private final UploadFileService uploadFileService;

    @GetMapping("/{chatId}")
    public String getChat(@PathVariable Long chatId, Model model) {
        model.addAttribute("bodyContent", "chat");
        model.addAttribute("chatId", chatId);
        model.addAttribute("prompts", promptService.getPromptsForChat(chatId));
        Chat chat = chatServiceRegistry.getCorrectChatService().findById(chatId);
        model.addAttribute("chat", chat);

        model.addAttribute("chats",chatServiceRegistry.getCorrectChatService().getChatsForCurrentUser());
        
        try{
            User currentUser = userService.getCurrentUser();
            if (currentUser instanceof RegisteredUser registeredUser){
                model.addAttribute("username", registeredUser.getUserFirstName());

                Subscription sub = subscriptionService.getActiveSubscription(registeredUser.getUserId());
                model.addAttribute("plan", sub.getPlan().getPlanName()); 
                model.addAttribute("user", registeredUser); 
            }
        } catch (Exception e) {
            
        }

        return "master-templateChat";
    }

    @PostMapping("/start")
    public String startChat(@RequestParam(value = "fileDocument", required = false) MultipartFile fileDocument, 
                            @RequestParam(value = "prompt", required = false) String prompt, 
                            Model model) throws Exception {

        if((prompt == null || prompt.isBlank()) && (fileDocument == null || fileDocument.isEmpty())){
            return "redirect:/home?error=You must enter a question and upload a document.";
        }
        
        if(prompt == null || prompt.isBlank()){
            return "redirect:/home?error=You must enter a question.";
        }
        if (fileDocument == null || fileDocument.isEmpty()) {
            return "redirect:/home?error=You must upload a document.";
        }

        Chat chat = chatServiceRegistry.getCorrectChatService().startNewChat();
        uploadFileService.saveFile(fileDocument, chat);
        promptService.createPrompt(chat.getId(), prompt);

        return "redirect:/chat/" + chat.getId();
    }

    @PostMapping("/message")
    public String sendMessage(@RequestParam Long chatId, @RequestParam String promptText) {
        promptService.createPrompt(chatId, promptText);
        return "redirect:/chat/" + chatId;
    }
    
    
    

}
