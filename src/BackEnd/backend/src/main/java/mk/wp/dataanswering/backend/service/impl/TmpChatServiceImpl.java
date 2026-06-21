package mk.wp.dataanswering.backend.service.impl;

import java.util.List;

import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.repository.PromptRepository;

import mk.wp.dataanswering.backend.service.RequestService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.model.Request;
import mk.wp.dataanswering.backend.model.Response;
import mk.wp.dataanswering.backend.model.TmpChat;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.repository.RequestRepository;
import mk.wp.dataanswering.backend.repository.ResponseRepository;
import mk.wp.dataanswering.backend.repository.TmpChatRepository;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.UserService;

@RequiredArgsConstructor
@Service
public class TmpChatServiceImpl implements ChatService<TmpChat, TmpUser> {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final TmpChatRepository tmpChatRepository;
    private final TmpUserRepository tmpUserRepository;
    private final RequestRepository requestRepository;
    private final ResponseRepository responseRepository;
    private final PromptRepository promptRepository;
    private final RequestService requestService;

    // TmpChatServiceImpl(ChatRepository chatRepository) {
    //     this.chatRepository = chatRepository;
    // }

    @Override
    public boolean supports() {
        return userService.getCurrentUser() instanceof TmpUser;
    }

    @Override
    public Chat startNewChat() {
        User currentUser = userService.getCurrentUser();
        if (!supports()) throw new InvalidUserException();
        TmpUser tmpUser = (TmpUser) currentUser;
        freeSpaceIfNeeded(tmpUser);
        TmpChat newChat = new TmpChat();
        tmpUser.setChat(newChat);
        tmpUserRepository.save(tmpUser);
        return newChat;
    }

    @Override
    public void freeSpaceIfNeeded(TmpUser tmpUser) {
        if (tmpUser.getChat() != null) {
            tmpUser.setChat(null);
            tmpUserRepository.save(tmpUser);
        }
    }

    @Override
    public boolean isChatLimitNotExceeded(TmpUser user) {
        return true;
    }

    @Override   
    public Chat findById(Long chatId){
        return tmpChatRepository.findById(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    @Override
    public List<TmpChat> getChatsForCurrentUser() {
        return List.of();
    }

    @Override
    public void addPrompt(Chat chat, Prompt prompt, Request request, Response response) throws RuntimeException {

        prompt.getRequests().add(request);
        prompt.setChat(chat);

        requestRepository.save(request);
        responseRepository.save(response);
        promptRepository.save(prompt);
        chatRepository.save(chat);
    }
}
