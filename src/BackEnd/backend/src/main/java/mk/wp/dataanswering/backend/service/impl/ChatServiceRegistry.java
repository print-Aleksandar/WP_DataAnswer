package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.exceptions.NoChatServiceFoundException;
import mk.wp.dataanswering.backend.service.ChatService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatServiceRegistry {
    // Strategy Design Pattern for more context

    private final List<ChatService<? extends User>> chatServices;

    public ChatServiceRegistry(List<ChatService<? extends User>> chatServices) {
        this.chatServices = chatServices;
    }

    public ChatService<? extends User> getCorrectChatService() {
        return chatServices.stream()
                .filter(s -> s.supports())
                .findFirst()
                .orElseThrow(() -> new NoChatServiceFoundException());
    }
}
