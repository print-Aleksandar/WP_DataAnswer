package mk.wp.dataanswering.backend.web.controler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.Resource;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.model.Response;
import mk.wp.dataanswering.backend.model.UploadedFile;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.dto.LlmRequest;
import mk.wp.dataanswering.backend.model.dto.MessageDto;
import mk.wp.dataanswering.backend.model.dto.PromptRequest;
import mk.wp.dataanswering.backend.model.dto.ToolCallDto;
import mk.wp.dataanswering.backend.service.LlmService;
import mk.wp.dataanswering.backend.service.UploadFileService;
import mk.wp.dataanswering.backend.service.UserService;
import mk.wp.dataanswering.backend.service.impl.ChatServiceRegistry;
import mk.wp.dataanswering.backend.service.MinioService;
import mk.wp.dataanswering.backend.service.PromptService;
import mk.wp.dataanswering.backend.service.ToolCallService;

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
    private final ToolCallService toolCallService;
    private ObjectMapper objectMapper= new ObjectMapper();

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

        Prompt req;

        try {
            req = promptService.createPrompt(chat.getId(), promptRequest.promptText());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .header(HttpHeaders.TRANSFER_ENCODING, "chunked")
                .body(getStreamResponse(promptRequest, user, req));
    }

    //*
    @PostMapping("/prompt/regenarate/last")
    public ResponseEntity<StreamingResponseBody> regeneratePrompt(
            @RequestBody PromptRequest promptRequest
    ) {
        Chat chat = chatServiceRegistry.getCorrectChatService().findById(promptRequest.chatId());
        User user = userService.getCurrentUser();

        Prompt req = promptService.regeneratePrompt(
                chat.getId(),
                promptRequest.promptText()
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .header(HttpHeaders.TRANSFER_ENCODING, "chunked")
                .body(getStreamResponse(promptRequest, user, req));
    }
    //*/


    private StreamingResponseBody getStreamResponse(PromptRequest promptRequest, User user, Prompt req) {
        return (OutputStream outputStream) -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
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

            List<ToolCallDto> toolCalls = Collections.emptyList();

            try {
                toolCalls = llmService.streamPrompt(
                    new LlmRequest(
                        user.getUserId(), 
                        promptRequest.chatId(),
                        promptRequest.promptText(),
                        history
                    ), 
                    helperStream
                );

            } catch (Exception e) {
                stopped = e.getClass().getName().contains("ClientAbortException");

                Thread.currentThread().interrupt();
                // System.out.print("ERROR: " + e.getMessage());
            } finally {
                try{
                    // String responseText = baos.toString(StandardCharsets.UTF_8);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    JsonParser parser = objectMapper.getFactory().createParser(bais);
                    
                    MappingIterator<JsonNode> chunks = objectMapper.readValues(parser, JsonNode.class);
                    StringBuilder responseText = new StringBuilder();
                    
                    while (chunks.hasNext()) {
                        JsonNode node= chunks.next();
                        
                        if (node.has("token")) 
                            responseText.append(node.get("token").asText());
                    }

                    Response res = promptService.saveResult(req.getId(), responseText.toString(), stopped);

                    toolCallService.saveAllToResponse(toolCalls, res);    
                } catch ( Exception e) {
                    System.out.println("[TOOL_CALLING_SAVE]: " + e.getMessage());
                }
            }
        };

    }
}