package mk.wp.dataanswering.backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import mk.wp.dataanswering.backend.model.Message;

public interface MessageService {

    Message findById(Long id);
    
    List<Message> findAllByChatId(Long chatId);

    Message createMessage(Long chatId, String question, List<MultipartFile> files);

    Message updateAnswer(Long messageId, String answer);

}
