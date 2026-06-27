package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.model.Response;
import mk.wp.dataanswering.backend.model.dto.MessageDto;

public interface PromptService {
    Prompt createPrompt(Long chatId, String promptText);
    Prompt regeneratePrompt(Long chatId, String promptText);
    Response saveResult(Long promptId, String responseText, boolean isStopped, Long TokenUsage);
    List<Prompt> getPromptsForChat(Long chatId);
    List<MessageDto> createHistory(List<Prompt> prompts);
    int getUsedTokens(Long userId);
    boolean isTokenLimitNotExceeded(Long userId);
}
