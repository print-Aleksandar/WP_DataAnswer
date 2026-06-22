package mk.wp.dataanswering.backend.service.impl;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import mk.wp.dataanswering.backend.repository.*;

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
    private final UploadedFileRepository uploadedFileRepository;

    // TmpChatServiceImpl(ChatRepository chatRepository) {
    //     this.chatRepository = chatRepository;
    // }

    @Override
    public boolean supports() {
        return userService.getCurrentUser() instanceof TmpUser;
    }

    @Override
    @Transactional
    public Chat startNewChat() {
        User currentUser = userService.getCurrentUser();
        if (!supports()) throw new InvalidUserException();
        TmpUser tmpUser = (TmpUser) currentUser;

        freeSpaceIfNeeded(tmpUser);

        TmpChat newChat = new TmpChat();
        newChat.setUser(tmpUser);
        tmpChatRepository.save(newChat);
        return newChat;
    }

    @Override
    @Transactional
    public void freeSpaceIfNeeded(TmpUser tmpUser) {
        Long userId = tmpUser.getUserId();
        responseRepository.deleteResponsesByTmpUserId(userId);
        requestRepository.deleteRequestsByTmpUserId(userId);
        promptRepository.deletePromptsByTmpUserId(userId);
        uploadedFileRepository.deleteByTmpUserId(userId);
        tmpChatRepository.deleteByTmpUserId(userId);
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
