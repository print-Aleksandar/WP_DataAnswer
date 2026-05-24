package mk.wp.dataanswering.backend.service.impl;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.SavedChat;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.repository.SavedChatRepository;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SavedChatServiceImpl implements ChatService<SavedChat, RegisteredUser, SavedChatRepository> {

    @Value("${registered.user.max.saved.chats}")
    private int maxSavedChats;

    private final UserService userService;
    private final SavedChatRepository savedChatRepository;

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
        SavedChat newChat = new SavedChat();
        newChat.setUser(registeredUser);
        savedChatRepository.save(newChat);
        return newChat;
    }

    @Override
    public void freeSpaceIfNeeded(RegisteredUser registeredUser) {
        List<SavedChat> chats = savedChatRepository.findSavedChatsByUserUserId(registeredUser.getUserId());
        if (chats.size() >= 5) {
            SavedChat oldest = chats.stream()
                    .min(Comparator.comparing(Chat::getLastModifiedTs))
                    .orElseThrow();
            oldest.setUser(null);
            savedChatRepository.save(oldest);
        }
    }

    @Override
    public SavedChatRepository getCorrectChatRepository() {
        return savedChatRepository;
    }
}
