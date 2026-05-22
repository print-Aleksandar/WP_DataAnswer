package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.enums.ChatType;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.UserService;

import java.util.Comparator;
import java.util.List;

public class ChatServiceImpl implements ChatService {

    private final AuthUtils authUtils;
    private final UserService userService;
    private final ChatRepository chatRepository;

    public ChatServiceImpl(AuthUtils authUtils, UserService userService, ChatRepository chatRepository) {
        this.authUtils = authUtils;
        this.userService = userService;
        this.chatRepository = chatRepository;
    }

    public void DeleteOldTmpChatIfExists(User user) {
        try {
            TmpUser tmpUser = (TmpUser) user;
            tmpUser.setChat(null);
        } catch (Exception ignored) { }
    }

    public void DeleteOldChatIfNumberExceeds(User user) {
        try {
            RegisteredUser registeredUser = (RegisteredUser) user;
            List<Chat> userChats =  registeredUser.getChats();
            userChats = userChats.stream()
                    .sorted(Comparator.comparing(Chat::getLastModifiedTs))
                    .toList();
            if (userChats.size() >= 50) {
                chatRepository.deleteById(userChats.getFirst().getId());
            }
        } catch (Exception ignored) { }
    }

    public void AddNewChatToAbstractUser(User user, Chat chat) {

    }

    @Override
    public Chat startNewChat() {
        User currentUser = userService.getCurrentUser();
        ChatType chatType = authUtils.isLoggedIn() ? ChatType.SAVED : ChatType.TEMPORARY;
        if (chatType == ChatType.TEMPORARY) {
            DeleteOldTmpChatIfExists(currentUser);
        }
        Chat newChat = new Chat(currentUser, chatType);
        chatRepository.save(newChat);
        return newChat;
    }
}
