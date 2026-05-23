package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.enums.ChatType;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class TmpUserChatServiceImpl implements ChatService<TmpUser> {

    private final UserService userService;
    private final ChatRepository chatRepository;
    private final TmpUserRepository tmpUserRepository;

    public TmpUserChatServiceImpl(UserService userService,
                                  ChatRepository chatRepository, TmpUserRepository tmpUserRepository) {
        this.userService = userService;
        this.chatRepository = chatRepository;
        this.tmpUserRepository = tmpUserRepository;
    }

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
        Chat newChat = new Chat(ChatType.TEMPORARY);
        chatRepository.save(newChat);
        tmpUser.setChat(newChat);
        tmpUserRepository.save(tmpUser);
        return newChat;
    }

    @Override
    public void freeSpaceIfNeeded(TmpUser tmpUser) {
        if (tmpUser.getChat() != null) {
            chatRepository.delete(tmpUser.getChat());
            tmpUser.setChat(null);
            tmpUserRepository.save(tmpUser);
        }
    }
}
