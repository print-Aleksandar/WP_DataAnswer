package mk.wp.dataanswering.backend.model.dto;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

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

    public LlmRequest(Long userId, Long chatId, String prompt, List<MessageDto> history) {
        this.chatId = chatId;
        this.userId = userId;
        this.prompt = prompt;
        this.history = LlmRequest.formatHistory(history);
    }

    public static List<Map<String, String>> formatHistory(List<MessageDto> history) {
        List<Map<String, String>> res = new ArrayList<>();
        history.forEach(m -> {
            Map<String, String> entry = new HashMap<>();

            entry.put("role", "user");
            entry.put("content", m.question);


            entry.put("role", "assistant");
            entry.put("content", m.response);

            res.add(entry);
        });

        return res;
    }
}
