package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.Chat;

public interface ChatService {
    
    List<Chat> listChats();

    List<Chat> listByClientId(Long clientId);

    Chat findById(Long id);
    Chat create(Long clientId, String chatName);
    void delete(Long id);
    Chat update(Long id, String chatName, Long clientId);

    public Chat addMessage(Long chatId, Long messageId);

}
