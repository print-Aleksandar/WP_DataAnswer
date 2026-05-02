package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.Chat;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatService {
    
    List<Chat> listChats();

    List<Chat> listByClientId(Long clientId);

    Chat findById(Long id);
    Chat create(Long clientId, String chatName);
    void delete(Long id);
    Chat update(Long id, String chatName);

}
