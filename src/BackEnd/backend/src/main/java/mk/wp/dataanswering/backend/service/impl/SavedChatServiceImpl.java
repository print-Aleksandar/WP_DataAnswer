package mk.wp.dataanswering.backend.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import mk.wp.dataanswering.backend.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Response;
import mk.wp.dataanswering.backend.model.SavedChat;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.exceptions.ExceededDayChatLimitException;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.UserService;

@RequiredArgsConstructor
@Service
public class SavedChatServiceImpl implements ChatService<SavedChat, RegisteredUser> {

    private final RegisteredUserRepository registeredUserRepository;
    @Value("${saved.chats.limit}")
    private int savedChatsLimit;

    private final UserService userService;
    private final SavedChatRepository savedChatRepository;
    private final ResponseRepository responseRepository;
    private final PromptRepository promptRepository;

    @Override
    public boolean supports() {
        return userService.getCurrentUser() instanceof RegisteredUser;
    }

    @Override
    public Chat startNewChat() {
        User currentUser = userService.getCurrentUser();
        if (!supports()) {
            throw new InvalidUserException();
        }
        RegisteredUser registeredUser = (RegisteredUser) currentUser;
        // TODO: da ne mozhe da pochne ako nema tokeni
        freeSpaceIfNeeded(registeredUser);
        SavedChat newChat = new SavedChat(registeredUser);
        savedChatRepository.save(newChat);
        return newChat;
    }

    @Override
    public void freeSpaceIfNeeded(RegisteredUser registeredUser) {
        List<SavedChat> chats = savedChatRepository.findSavedChatsByUserUserId(registeredUser.getUserId());
        if (chats.size() >= savedChatsLimit) {
            SavedChat oldest = chats.stream()
                    .min(Comparator.comparing(Chat::getLastModifiedTs))
                    .orElseThrow();

            unlinkChatFromRegisteredUser(registeredUser, oldest);
        }
    }

    @Override
    public void unlinkChatFromRegisteredUser(RegisteredUser registeredUser, SavedChat savedChat) {
        if (!savedChatRepository.existsByIdAndCreatedByUserId(savedChat.getId(), registeredUser.getUserId())) {
            throw new RuntimeException();
        }
        savedChat.setUser(null);
        savedChatRepository.save(savedChat);
    }

    @Override
    public Chat findById(Long chatId){
        return savedChatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    @Override
    public List<SavedChat> getChatsForCurrentUser() {
        RegisteredUser user = (RegisteredUser) userService.getCurrentUser();
        return savedChatRepository.findSavedChatsByUserUserId(user.getUserId());
    }

    @Override
    public void addPrompt(Chat chat, Prompt prompt, Response response) {
        prompt.setChat(chat);

        responseRepository.save(response);
        promptRepository.save(prompt);
        savedChatRepository.save((SavedChat) chat);
    }
}
