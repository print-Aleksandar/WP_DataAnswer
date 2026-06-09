package mk.wp.dataanswering.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.repository.PromptRepository;
import mk.wp.dataanswering.backend.service.PromptService;

@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService{
    
    private final PromptRepository promptRepository;
    private final ChatRepository chatRepository;

    @Override
    public Prompt createPrompt(Long chatId, String promptText) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Prompt prompt = new Prompt();
        prompt.setChat(chat);
        prompt.setPromptText(promptText);

        return promptRepository.save(prompt);
    }

    @Override
    public List<Prompt> getPromptsForChat(Long chatId) {
        return promptRepository.findByChatIdOrderByPromptTsAsc(chatId);
    }

}
