package mk.wp.dataanswering.backend.service.impl;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.TmpChat;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.repository.TmpChatRepository;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TmpChatServiceImpl implements ChatService<TmpChat, TmpUser> {

    private final UserService userService;
    private final TmpChatRepository tmpChatRepository;
    private final TmpUserRepository tmpUserRepository;

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
    public boolean isChatLimitExceeded(TmpUser user) {
        return true;
    }
}
