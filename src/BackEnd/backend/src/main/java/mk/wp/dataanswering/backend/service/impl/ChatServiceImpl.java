package mk.wp.dataanswering.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Client;
import mk.wp.dataanswering.backend.model.Message;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.repository.ClientRepository;
import mk.wp.dataanswering.backend.repository.MessageRepository;
import mk.wp.dataanswering.backend.service.ChatService;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final ClientRepository clientRepository;
    private final MessageRepository messageRepository;

    @Override
    public List<Chat> listChats() {
        return chatRepository.findAll();
    }

    @Override
    public List<Chat> listByClientId(Long clientId) {
        return chatRepository.findByClientId(clientId);
    }

    @Override
    public Chat findById(Long id) {
        return chatRepository.findById(id).orElseThrow( () -> new RuntimeException("Chat with id " + id + " was not found."));
    }

    @Override
    public Chat create(Long clientId, String chatName) {
        if (clientId == null || chatName == null) {
            throw new IllegalArgumentException();
        }
        Client client = clientRepository.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Client with id " + clientId + " was not found."));
        Chat chat = new Chat(client, chatName);
        return chatRepository.save(chat);
    }

    @Override
    public void delete(Long id) {
        chatRepository.deleteById(id);
    }

    @Override
    public Chat update(Long id, String chatName) {
        if (id == null || chatName == null) {
            throw new IllegalArgumentException();
        }

        Chat chat = chatRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Chat with id " + id + " was not found."));
        chat.setChatName(chatName);

        return chatRepository.save(chat);
    }
    
}
