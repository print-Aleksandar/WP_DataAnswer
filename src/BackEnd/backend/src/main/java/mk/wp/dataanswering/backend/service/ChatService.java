package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.User;

public interface ChatService<T extends User> {
    boolean supports();
    Chat startNewChat();
    void freeSpaceIfNeeded(T user);
}
