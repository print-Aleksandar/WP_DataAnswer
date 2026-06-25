package mk.wp.dataanswering.backend.web.controler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ResponseEntity;
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
import mk.wp.dataanswering.backend.model.UploadedFile;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.dto.LlmRequest;
import mk.wp.dataanswering.backend.model.dto.MessageDto;
import mk.wp.dataanswering.backend.model.dto.PromptRequest;
import mk.wp.dataanswering.backend.service.LlmService;
import mk.wp.dataanswering.backend.service.UploadFileService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;
import mk.wp.dataanswering.backend.service.MinioService;
import mk.wp.dataanswering.backend.service.PromptService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private final PromptService promptService;

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
    public ResponseEntity<StreamingResponseBody> streamResponse(
            @RequestBody PromptRequest promptRequest
    ) {
        Chat chat = chatServiceRegistry.getCorrectChatService().findById(promptRequest.chatId());
        User user = userService.getCurrentUser();

        Request reqDummy = null;

        try {
            reqDummy = promptService.createPrompt(chat.getId(), promptRequest.promptText());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        Request req = reqDummy;


        StreamingResponseBody responseBody = (OutputStream outputStream) -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean corrupted = false;
            boolean stopped = false;

            // Write to both streams
            OutputStream helperStream = new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                    baos.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    outputStream.write(b, off, len);
                    baos.write(b, off, len);
                }

                @Override
                public void flush() throws IOException {
                    outputStream.flush();
                    // baos.flush();
                }
            };

            try {
                llmService.streamPrompt(new LlmRequest(
                    user.getUserId(), 
                    promptRequest.chatId(),
                    promptRequest.promptText(),
                    promptService.createHistory(
                        promptService.getPromptsForChat(promptRequest.chatId())
                    )
                ), helperStream);

            } catch (Exception e) {
                stopped = e.getClass().getName().contains("ClientAbortException");
                corrupted = !stopped;

                helperStream.write(
                        ("\n[ERROR] " + e.getMessage()).getBytes(StandardCharsets.UTF_8)
                );

                Thread.currentThread().interrupt();
                System.out.print("ERROR: " + e.getMessage());


            } finally {
                // get set response text to response object
                String responseText = baos.toString(StandardCharsets.UTF_8);
                // response.setResponseText(responseText);
                // response.setAnswered(true);
                promptService.saveResult(req.getId(), responseText, corrupted, stopped);
            }

            // chatServiceRegistry.getCorrectChatService().addPrompt(chat, prompt, req, response);

        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .header(HttpHeaders.TRANSFER_ENCODING, "chunked")
                .body(responseBody);
    }

    //*
    @PostMapping("/prompt/regenarate/last")
    public ResponseEntity<StreamingResponseBody> regeneratePrompt(
            @RequestBody PromptRequest promptRequest
    ) {
        Chat chat = chatServiceRegistry.getCorrectChatService().findById(promptRequest.chatId());
        User user = userService.getCurrentUser();



        Request req = promptService.regeneratePrompt(
                chat.getId(),
                promptRequest.promptText()
        );


        StreamingResponseBody responseBody = (OutputStream outputStream) -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean corrupted = false;
            boolean stopped = false;

            // Write to both streams
            OutputStream helperStream = new OutputStream() {
                @Override
                public void write(int b) throws IOException {
                    outputStream.write(b);
                    baos.write(b);
                }

                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    outputStream.write(b, off, len);
                    baos.write(b, off, len);
                }

                @Override
                public void flush() throws IOException {
                    outputStream.flush();
                    // baos.flush();
                }
            };

            List<MessageDto> history = promptService.createHistory(
                    promptService.getPromptsForChat(promptRequest.chatId())
            );

            if (!history.isEmpty())
                history.removeLast();

            try {
                llmService.streamPrompt(new LlmRequest(
                    user.getUserId(), 
                    promptRequest.chatId(),
                    promptRequest.promptText(),
                    history
                ), helperStream);

            } catch (Exception e) {
                stopped = e.getClass().getName().contains("ClientAbortException");
                corrupted = !stopped;

                Thread.currentThread().interrupt();
                // System.out.print("ERROR: " + e.getMessage());
            } finally {
                // get set response text to response object
                String responseText = baos.toString(StandardCharsets.UTF_8);
                // response.setResponseText(responseText);
                // response.setAnswered(true);
                promptService.saveResult(req.getId(), responseText, corrupted, stopped);
            }

            // chatServiceRegistry.getCorrectChatService().addPrompt(chat, prompt, req, response);

        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .header(HttpHeaders.TRANSFER_ENCODING, "chunked")
                .body(responseBody);
    }
    //*/
}