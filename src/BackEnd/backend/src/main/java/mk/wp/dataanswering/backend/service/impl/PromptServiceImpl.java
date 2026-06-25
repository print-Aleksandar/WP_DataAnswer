package mk.wp.dataanswering.backend.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    @Override
    @Transactional
    public Prompt createPrompt(Long chatId, String promptText) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id " + chatId));

        if (!requestService.isNotRequestLimitForChatExceeded(chat)) {
            throw new RuntimeException("RequestLimitExceeded");
        }

        Prompt prompt = new Prompt(promptText, chat);
        promptRepository.save(prompt);

        Request req = new Request();
        req.setPrompt(prompt);

        return requestRepository.save(req);
    }

    @Override
    @Transactional
    public void saveResult(Long requestId, String responseText, boolean corrupted, boolean stopped) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found with id " + requestId));
        request.setStopped(stopped);

        Response response = new Response();
        response.setRequest(request);
        response.setResponseText(responseText);
        response.setCorrupted(corrupted);
        response.setAnswered(!corrupted);
        response.setAnswerable(true);

        responseRepository.save(response);
        requestRepository.save(request);
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
                        .map( p -> {
                            List<Request> req = p.getRequests()  == null ?
                                    Collections.emptyList() : p.getRequests()
                                    ;
                            return new MessageDto(
                                    p.getPromptText(),
                                    req.stream()
                                            .map(Request::getResponse)
                                            .filter(r -> r != null)
                                            .map(Response::getResponseText)
                                            .findFirst() // TODO Mosh treba podobra logika
                                            .orElse(null)
                            );
                        }).toList()
        );
    }

    @Override
    @Transactional
    public Request regeneratePrompt(Long chatId, String promptText) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat not found with id " + chatId));

        return promptRepository.findAllByChatId(chat.getId())
                .reversed()
                .stream()
                .flatMap(p -> p.getRequests().stream())
                .filter(r -> r.getResponse() == null)
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("No prompt with null response")
                )
                ;

    }

}