package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.service.ChatService;
import org.springframework.stereotype.Service;

@Service
public class RegisteredUserChatService implements ChatService<RegisteredUser> {
    @Override
    public Chat startNewChat() {
        return null;
    }
}
