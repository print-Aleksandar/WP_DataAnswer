package mk.wp.dataanswering.backend.web.controler;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.dto.MessageDto;
import mk.wp.dataanswering.backend.service.PromptService;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.UploadFileService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;
import mk.wp.dataanswering.backend.service.ExternalToolService;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatServiceRegistry chatServiceRegistry;
    private final UserService userService;
    private final PromptService promptService;
    private final SubscriptionService subscriptionService;
    private final UploadFileService uploadFileService;
    private final AuthUtils authUtils;


    @GetMapping("/{chatId}")
    public String getChat(@PathVariable Long chatId, Model model) {
        model.addAttribute("bodyContent", "chat");

        List<MessageDto> history = promptService.createHistory(
            promptService.getPromptsForChat(chatId)
        ); 
        model.addAttribute("prompts", history);
    
        Chat chat = chatServiceRegistry.getCorrectChatService().findById(chatId);
        model.addAttribute("chat", chat);

        model.addAttribute("headerText", "");

        model.addAttribute("chats",chatServiceRegistry.getCorrectChatService().getChatsForCurrentUser());
        
        try{
            if (authUtils.isLoggedIn()){
                RegisteredUser registeredUser = authUtils.getCurrentRegisteredUser();
                model.addAttribute("userId", registeredUser.getUserId());

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
        User user = userService.getCurrentUser();

        try {
            uploadFileService.saveFile(fileDocument, user, chat);
        } catch (Exception e) {
            return "redirect:/home?error=Upload Failed!";
        }
        
        promptService.createPrompt(chat.getId(), prompt);

        return "redirect:/chat/" + chat.getId();
    }

    @PostMapping("/message")
    public String sendMessage(@RequestParam Long chatId, @RequestParam String promptText) {
        promptService.createPrompt(chatId, promptText);
        return "redirect:/chat/" + chatId;
    }
    
    
    

}
