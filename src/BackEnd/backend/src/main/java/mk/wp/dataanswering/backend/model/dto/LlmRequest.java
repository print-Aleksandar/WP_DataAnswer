package mk.wp.dataanswering.backend.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Data
public class LlmRequest {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("chat_id")
    private Long chatId;

    private String prompt;

    private List<Map<String, String>> history = new ArrayList<>();

    @JsonProperty("max_tokens")
    private int maxTokens = 1024;

    private double temperature = 0.7;

    public LlmRequest(Long userId, Long chatId, String prompt) {
        this.chatId = chatId;
        this.userId = userId;
        this.prompt = prompt;
    }
}
