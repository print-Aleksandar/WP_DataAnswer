package mk.wp.dataanswering.backend.web.controler;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mk.wp.dataanswering.backend.model.*;
import mk.wp.dataanswering.backend.model.exceptions.ExceededDayChatLimitException;
import mk.wp.dataanswering.backend.model.exceptions.InChatTokenExceededException;
import mk.wp.dataanswering.backend.model.exceptions.InvalidArgumentsException;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.service.*;
import mk.wp.dataanswering.backend.service.impl.SavedChatServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.dto.MessageDto;
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
    private final AuthUtils authUtils;
    private final ChatService<SavedChat, RegisteredUser> savedChatService;
    private final SavedChatServiceImpl savedChatServiceImpl;
    private final LlmService llmService;


    @GetMapping("/{chatId}")
    public String getChat(@PathVariable Long chatId, @RequestParam(value="error", required=false) String error, Model model) {
        model.addAttribute("bodyContent", "chat");



        List<MessageDto> history = promptService.createHistory(
            promptService.getPromptsForChat(chatId)
        ); 
        model.addAttribute("prompts", history);

        Chat chat = null;
        try {
           chat = chatServiceRegistry.getCorrectChatService().findById(chatId);
        } catch (RuntimeException e) {
            return "redirect:/login";
        }

        model.addAttribute("chat", chat);



        List<? extends Chat> savedChats = chatServiceRegistry.getCorrectChatService().getChatsForCurrentUser();
        if (!savedChats.stream().map(c -> c.getId()).toList().contains(chatId))
        {
            return "redirect:/login";
        }

        model.addAttribute("headerText", "");

        if (error != null)
        {
            model.addAttribute("error", error);

        }

        model.addAttribute("chats",chatServiceRegistry.getCorrectChatService().getChatsForCurrentUser());

        model.addAttribute("disabled", false);
        model.addAttribute("limitTill", null);

        User current = userService.getCurrentUser();
        long currentId = current.getUserId();
        try {
            if (subscriptionService.getActiveSubscription(currentId) != null) {
                promptService.isTokenLimitNotExceeded(userService.getCurrentUser().getUserId());
                if ((error != null && error.contains("Token Exceeded")) ||
                        userService.getCurrentUser().getLimitTill() != null) {
                    model.addAttribute("disabled", true);
                    model.addAttribute("limitTill", userService.getCurrentUser().getLimitTill());
                }
            }
        } catch (Exception e) {
            subscriptionService.subscribeToGuest(currentId);
        }

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

        Chat chat = null;
        try {
            chat = chatServiceRegistry.getCorrectChatService().startNewChat();
        } catch (RuntimeException e) {
            return "redirect:/home?error=Token limit exceeded.";
        }
        if (chat == null) {
            return "redirect:/home?error=Error occured.";
        }

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
    public String sendMessage(
            @RequestParam Long chatId,
            @RequestParam String promptText,
            HttpServletResponse response) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);


        try {
            promptService.createPrompt(chatId, promptText);
        } catch (RuntimeException e) {
            return "redirect:/chat/" + chatId + "?error=" + e.getMessage();
        }
        return "redirect:/chat/" + chatId;
    }

    @PostMapping("/unlink")
    public String unlinkChat(@RequestParam Long chatId,
                             HttpServletRequest request) {

        RegisteredUser currentUser;
        try {
            currentUser = authUtils.getCurrentRegisteredUser();
        } catch (Exception e) {
            return "redirect:/login";
        }

        SavedChat chat = (SavedChat) chatServiceRegistry
                .getCorrectChatService()
                .findById(chatId);

        if (!chat.getUser().getUserId()
                .equals(userService.getCurrentUser().getUserId())) {
            return "redirect:/login";
        }

        savedChatServiceImpl.unlinkChatFromRegisteredUser(currentUser, chat);

        String referer = request.getHeader("Referer");

        return "redirect:" + (referer != null ? referer : "/home");
    }

    @PostMapping("/delete-all")
    public String deleteForUser() {
        RegisteredUser current = (RegisteredUser) userService.getCurrentUser();
        chatServiceRegistry.getCorrectChatService().getChatsForCurrentUser().stream()
                .forEach(c -> savedChatService.unlinkChatFromRegisteredUser(current, (SavedChat) c));

        return "redirect:/user/" + current.getUserId();
    }

    @ResponseBody
    @PostMapping("/rename/{chatId}")
    public String renameChat(@PathVariable Long chatId, @RequestBody(required = false) String newName) {
        
        
        if(newName == null) {
            Prompt prompt = promptService.getPromptsForChat(chatId).getFirst();
            Response response = prompt.getResponse();
            newName = llmService.generateChatTitle(prompt.getPromptText(), response.getResponseText());
        } 
        
        Chat chat = chatServiceRegistry.getCorrectChatService().findById(chatId);
        chat.setChatName(newName);
        chatServiceRegistry.getCorrectChatService().updateChat(chat);

        return newName;
    }
    
}
