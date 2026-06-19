package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.model.dto.MessageDto;

public interface PromptService {
    Prompt createPrompt(Long chatId, String promptText);
    List<Prompt> getPromptsForChat(Long chatId);
    List<MessageDto> createHistory(List<Prompt> prompts);
}
