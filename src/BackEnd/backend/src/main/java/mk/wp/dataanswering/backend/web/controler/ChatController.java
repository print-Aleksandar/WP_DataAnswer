package mk.wp.dataanswering.backend.web.controler;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Client;
import mk.wp.dataanswering.backend.model.Message;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.MessageService;
import mk.wp.dataanswering.backend.service.MinioService;
import mk.wp.dataanswering.backend.service.UploadedFileService;




@Controller
@RequestMapping(path = {"/chat"})
@AllArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    private final UploadedFileService uploadedFileService;
    private final MinioService minioService;
    private final MessageService messageService;

    @GetMapping("/{chatId}")
    public String getChat(@PathVariable Long chatId, Model model, Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        Chat chat = chatService.findById(chatId);
        List<Message> messages = messageService.findAllByChatId(chatId);
        List<Chat> chats = chatService.listByClientId(client.getId());

        model.addAttribute("chat", chat);
        model.addAttribute("messages", messages);
        model.addAttribute("chats", chats);
        model.addAttribute("bodyContent", "chat");
        return "master-template";
    }
    
    @PostMapping("/create")
    public String createChat(@RequestParam String chatName,
                            Authentication authentication
    ) {
        Client client = (Client) authentication.getPrincipal();
        chatService.create(client.getId(), chatName);
        return "redirect:/home";
    }
    
    @PostMapping("/{chatId}/message")
    public String uploadFile(@PathVariable Long chatId, 
                            @RequestParam("question") String question,
                            @RequestParam("file") List<MultipartFile> files,
                            Authentication authentication) throws Exception {

        Message message = messageService.createMessage(chatId, question);

        for (MultipartFile file : files){
            if (file.isEmpty()) continue;

            String minioKey = minioService.uploadFile(file);

            String fileName = file.getOriginalFilename();

            String processType = (fileName.endsWith(".xlsx") || 
                              fileName.endsWith(".csv"))
                              ? "structured" : "rag";

            uploadedFileService.storeFile(
                message.getId(),
                fileName,
                file.getContentType(),
                processType,
                minioKey
            );
        }

        return "redirect:/chat/" + chatId;
    }
    
}
