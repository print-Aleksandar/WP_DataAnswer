package mk.wp.dataanswering.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Message;
import mk.wp.dataanswering.backend.repository.MessageRepository;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.MessageService;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatService chatService;

    @Override
    public Message findById(Long id) {
        return messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Message with id " + id + " was not found."));
    }

    @Override
    public List<Message> findAllByChatId(Long chatId) {
        return messageRepository.findByChatIdOrderBySequenceNoAsc(chatId);
    }

    @Override
    public Message createMessage(Long chatId, String question, List<MultipartFile> files) {
        if (chatId == null || question == null) {
            throw new IllegalArgumentException();
        }
        Message message = new Message();
        message.setChat(chatService.findById(chatId));
        message.setQuestion(question);
        int sequenceNo = messageRepository.findByChatIdOrderBySequenceNoAsc(chatId).size();
        message.setSequenceNo(sequenceNo + 1); 
        return messageRepository.save(message);
    }

    @Override
    public Message updateAnswer(Long messageId, String answer) {
        if (messageId == null || answer == null) {
            throw new IllegalArgumentException();
        }
        Message message = messageRepository.findById(messageId).orElseThrow(() -> new IllegalArgumentException("Message with id " + messageId + " was not found."));
        message.setAnswer(answer);
        return messageRepository.save(message);
    }
    
}
