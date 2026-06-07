package mk.wp.dataanswering.backend.service.impl;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.*;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.repository.SavedChatRepository;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SavedChatServiceImpl implements ChatService<SavedChat, RegisteredUser, SavedChatRepository> {

    @Value("${saved.chats.limit}")
    private int savedChatsLimit;

    private final UserService userService;
    private final SavedChatRepository savedChatRepository;
    private final SubscriptionService subscriptionService;

    @Override
    public boolean supports() {
        return userService.getCurrentUser() instanceof RegisteredUser;
    }

    @Override
    public Chat startNewChat() {
        User currentUser = userService.getCurrentUser();
        if (!supports()) throw new InvalidUserException();
        RegisteredUser registeredUser = (RegisteredUser) currentUser;
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
            oldest.setUser(null);
            savedChatRepository.save(oldest);
        }
    }

    @Override
    public boolean isLimitExceeded(RegisteredUser registeredUser) {
        Subscription current = subscriptionService.getActiveSubscription(registeredUser.getUserId());
        // barame po created_user_id e fiksno, user_id e relaciska kolona na brishenje ja nema

    }

    @Override
    public SavedChatRepository getCorrectChatRepository() {
        return savedChatRepository;
    }
}
