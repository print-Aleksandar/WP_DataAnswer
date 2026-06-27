package mk.wp.dataanswering.backend.service.impl;

import java.util.List;

import jakarta.transaction.Transactional;
import mk.wp.dataanswering.backend.model.*;
import mk.wp.dataanswering.backend.repository.*;

import mk.wp.dataanswering.backend.service.TmpUserService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.UserService;

@RequiredArgsConstructor
@Service
public class TmpChatServiceImpl implements ChatService<TmpChat, TmpUser> {

    private final ChatRepository chatRepository;
    private final UserService userService;
    private final TmpChatRepository tmpChatRepository;
    private final ResponseRepository responseRepository;
    private final PromptRepository promptRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final TmpUserRepository tmpUserRepository;
    private final TmpUserService tmpUserService;

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
        tmpUser.setLimitTill(null);
        tmpUserRepository.save(tmpUser);
        tmpUserService.cleanUpBeforeUserDeletion(tmpUser.getUserId());
    }

    @Override
    public Chat findById(Long chatId){
        return tmpChatRepository.findById(chatId)
            .orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    @Override
    public List<TmpChat> getChatsForCurrentUser() {
        return tmpChatRepository.getTmpChatsByUser_UserId(userService.getCurrentUser().getUserId());
    }

    @Override
    public void addPrompt(Chat chat, Prompt prompt, Response response) throws RuntimeException {
        prompt.setChat(chat);

        responseRepository.save(response);
        promptRepository.save(prompt);
        chatRepository.save(chat);
    }

    @Override
    public void unlinkChatFromRegisteredUser(RegisteredUser registeredUser, SavedChat savedChat) {

    }
}
