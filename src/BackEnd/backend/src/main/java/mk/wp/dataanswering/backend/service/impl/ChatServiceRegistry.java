package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceRegistry {

    private final List<ChatService<? extends User>> chatServices;

    public ChatServiceRegistry(List<ChatService<? extends User>> chatServices) {
        this.chatServices = chatServices;
    }

    public ChatService<? extends User> getCorrectChatService(User u) {
        return chatServices.stream()
                .filter(s -> s.supports(u))
                .findFirst()
                .orElseThrow();
    }
}
