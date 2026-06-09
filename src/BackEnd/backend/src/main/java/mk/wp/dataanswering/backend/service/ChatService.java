package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.User;

public interface ChatService<T extends Chat, R extends User> {

    boolean supports();
    Chat startNewChat();
    void freeSpaceIfNeeded(R user);
    public boolean isChatLimitNotExceeded(R user);
    Chat findById(Long chatId);
    List<? extends Chat> getChatsForCurrentUser();
}
