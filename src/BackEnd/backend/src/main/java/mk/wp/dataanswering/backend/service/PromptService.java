package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.model.dto.MessageDto;

public interface PromptService {
    Prompt createPrompt(Long chatId, String promptText);
    Prompt regeneratePrompt(Long chatId, String promptText);
    void saveResult(Long requestId, String responseText, boolean corrupted, boolean stopped);
    List<Prompt> getPromptsForChat(Long chatId);
    List<MessageDto> createHistory(List<Prompt> prompts);
}
