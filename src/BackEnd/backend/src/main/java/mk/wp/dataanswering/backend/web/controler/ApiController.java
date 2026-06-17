package mk.wp.dataanswering.backend.web.controler;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.dto.LlmRequest;
import mk.wp.dataanswering.backend.service.LlmService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final LlmService llmService;
    private final UserService userService;
    private final ChatServiceRegistry chatServiceRegistry;


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

    @PostMapping("/prompt")
    public ResponseEntity<StreamingResponseBody> streamResponse(@RequestBody String prompt) {
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
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .header(HttpHeaders.TRANSFER_ENCODING, "chunked") 
                .body(responseBody);
    }
}
