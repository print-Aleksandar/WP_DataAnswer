package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.Prompt;

public interface PromptService {
    Prompt createPrompt(Long chatId, String promptText);
    List<Prompt> getPromptsForChat(Long chatId);
}
