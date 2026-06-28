package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.*;

public interface ChatService<T extends Chat, R extends User> {

    boolean supports();
    Chat startNewChat();
    void freeSpaceIfNeeded(R user);
    Chat findById(Long chatId);
    List<? extends Chat> getChatsForCurrentUser();
    void addPrompt(Chat chat, Prompt prompt, Response response);
    void unlinkChatFromRegisteredUser(RegisteredUser registeredUser, SavedChat savedChat);
    Chat updateChat(Chat chat);
}
