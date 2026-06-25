package mk.wp.dataanswering.backend.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import mk.wp.dataanswering.backend.service.SubscriptionService;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.model.Response;
import mk.wp.dataanswering.backend.model.dto.MessageDto;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.repository.PromptRepository;
import mk.wp.dataanswering.backend.repository.ResponseRepository;
import mk.wp.dataanswering.backend.service.PromptService;

@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService{

    private final PromptRepository promptRepository;
    private final ChatRepository chatRepository;
    private final ResponseRepository responseRepository;
    private final SubscriptionService subscriptionService;

    @Override
    @Transactional
    public Prompt createPrompt(Long chatId, String promptText) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id " + chatId));

        Prompt prompt = new Prompt(promptText, chat);
        return promptRepository.save(prompt);
    }

    @Override
    @Transactional
    public void saveResult(Long promptId, String responseText, boolean isStopped) {
        Prompt prompt = promptRepository.findById(promptId)
                .orElseThrow(() -> new EntityNotFoundException("Prompt not found with id " + promptId));
        // prompt.setStopped(stopped);

        Response response = new Response();
        response.setPrompt(prompt);
        response.setResponseText(responseText);
        response.setStopped(isStopped);
        prompt.setResponse(response);

        responseRepository.save(response);
    }


    @Override
    public List<Prompt> getPromptsForChat(Long chatId) {
        return promptRepository.findByChatIdOrderByPromptTsAsc(chatId);
    }

    @Override
    public List<MessageDto> createHistory(List<Prompt> prompts) {
        return new ArrayList<MessageDto>(
                prompts.stream()
                        .sorted(
                                Comparator.comparing(Prompt::getPromptTs)
                        )
                        .map( p -> 
                            new MessageDto(
                                    p.getPromptText(),
                                    p.getResponse() == null ? 
                                    null : 
                                    p.getResponse().getResponseText()
                            )
                        ).toList()
        );
    }

    @Override
    public int getUsedTokens(Long userId) {
        return responseRepository.findAllByUserId(userId).stream()
                .filter(p -> p.getPrompt().getPromptTs().isAfter(LocalDateTime.now().minusDays(1)))
                .mapToInt(Response::getTokenUsage).
                sum();
    }

    @Override
    public boolean isTokenLimitNotExceeded(Long userId) {
        return getUsedTokens(userId) < subscriptionService.getActiveSubscription(userId).getPlan().getTokens();
    }

    @Override
    @Transactional
    public Prompt regeneratePrompt(Long chatId, String promptText) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id " + chatId));

        return promptRepository.findAllByChatId(chat.getId())
                .reversed()
                .stream()
                .filter(r -> r.getResponse() == null)
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("No prompt with null response")
                )
                ;

    }



}