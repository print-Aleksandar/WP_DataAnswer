package mk.wp.dataanswering.backend.web.controler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.model.Request;
import mk.wp.dataanswering.backend.model.UploadedFile;
import mk.wp.dataanswering.backend.model.dto.LlmRequest;
import mk.wp.dataanswering.backend.service.LlmService;
import mk.wp.dataanswering.backend.service.UploadFileService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;
import mk.wp.dataanswering.backend.service.MinioService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final LlmService llmService;
    private final UserService userService;
    private final MinioService minioService;
    private final ChatServiceRegistry chatServiceRegistry;
    private final UploadFileService fileService;

    @GetMapping("/stream-chunks")
    @PreAuthorize("permitAll()")
    public ResponseEntity<StreamingResponseBody> streamChunks() {
        
        StreamingResponseBody responseBody = (OutputStream outputStream) -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    String chunk = "Chunk #" + i + " data packet\n";
                    outputStream.write(chunk.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush(); // Forces the chunk to be sent immediately
                    
                    // Simulate processing delay
                    Thread.sleep(500); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .header(HttpHeaders.TRANSFER_ENCODING, "chunked") 
                .body(responseBody);
    }

    @GetMapping("/download/file/{userId}/{chatId}")
    public ResponseEntity<Resource> downloadFileFromChat(@PathVariable Long userId, @PathVariable Long chatId) {
        Chat chat;
        UploadedFile file;
        try{
            chat = chatServiceRegistry.getCorrectChatService().findById(chatId);
            file = fileService.findByChat(chat);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        try {
            ByteArrayResource resource = new ByteArrayResource(
                minioService.downloadFile(
                    file.getMinioKey(), 
                    userId,
                    chatId
                ).readAllBytes()
            );

            return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/prompt")
    public ResponseEntity<StreamingResponseBody> streamResponse(@RequestBody String prompt) {
        // Prompt prompt = new Prompt(, prompt, null, null)
        // Request req;
        StreamingResponseBody responseBody = (OutputStream outputStream) -> {
                try {
                    llmService.streamPrompt(new LlmRequest(
                        userService.getCurrentUser().getUserId(), 
                        1L, // TODO: Get current active chat id for current user
                        prompt

                    ), outputStream);
                    
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    System.out.print("ERROR: " + e.getMessage());
                }

                // Add response to prompt 
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .header(HttpHeaders.TRANSFER_ENCODING, "chunked") 
                .body(responseBody);
    }
}
