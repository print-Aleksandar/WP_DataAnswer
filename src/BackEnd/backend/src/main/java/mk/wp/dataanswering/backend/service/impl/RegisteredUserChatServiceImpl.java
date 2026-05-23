package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.enums.ChatType;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class RegisteredUserChatServiceImpl implements ChatService<RegisteredUser> {

    @Value("${registered.user.max.saved.chats}")
    private int maxSavedChats;

    private final UserService userService;
    private final ChatRepository chatRepository;

    public RegisteredUserChatServiceImpl(UserService userService,
                                         ChatRepository chatRepository) {
        this.userService = userService;
        this.chatRepository = chatRepository;
    }

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
        //Chat newChat = new Chat(currentUser, ChatType.SAVED);
        //chatRepository.save(newChat);
        return null;
    }

    @Override
    public void freeSpaceIfNeeded(RegisteredUser registeredUser) {
        List<Chat> chats = registeredUser.getChats();
        if (chats != null && chats.size() >= maxSavedChats) {
            Chat oldest = chats.stream()
                    .min(Comparator.comparing(Chat::getLastModifiedTs))
                    .orElseThrow();
            oldest.setUser(null);
            chatRepository.save(oldest);
            chats.remove(oldest);
        }
    }
}
