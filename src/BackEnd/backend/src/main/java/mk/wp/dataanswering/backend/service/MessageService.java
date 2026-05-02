package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.Message;

import java.util.List;

public interface MessageService {
    
    List<Message> findAllByChatId(Long chatId);

    Message addMessage(Long chatId, String question, String answer);

    Message findById(Long id);

}
