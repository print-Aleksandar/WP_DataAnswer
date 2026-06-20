package mk.wp.dataanswering.backend.service.impl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Prompt;
import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.Request;
import mk.wp.dataanswering.backend.model.Response;
import mk.wp.dataanswering.backend.model.SavedChat;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.model.exceptions.ExceededDayChatLimitException;
import mk.wp.dataanswering.backend.model.exceptions.InvalidUserException;
import mk.wp.dataanswering.backend.repository.PromptRepository;
import mk.wp.dataanswering.backend.repository.RequestRepository;
import mk.wp.dataanswering.backend.repository.ResponseRepository;
import mk.wp.dataanswering.backend.repository.SavedChatRepository;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.service.ChatService;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.UserService;

@RequiredArgsConstructor
@Service
public class SavedChatServiceImpl implements ChatService<SavedChat, RegisteredUser> {

    @Value("${saved.chats.limit}")
    private int savedChatsLimit;

    private final UserService userService;
    private final SavedChatRepository savedChatRepository;
    private final SubscriptionService subscriptionService;
    private final RequestRepository requestRepository;
    private final ResponseRepository responseRepository;
    private final PromptRepository promptRepository;

    @Override
    public boolean supports() {
        return userService.getCurrentUser() instanceof RegisteredUser;
    }

    @Override
    public Chat startNewChat() {
        User currentUser = userService.getCurrentUser();
        if (!supports()) {
            throw new InvalidUserException();
        }
        RegisteredUser registeredUser = (RegisteredUser) currentUser;
        if (!isChatLimitNotExceeded(registeredUser)) {
            throw new ExceededDayChatLimitException();
        }
        freeSpaceIfNeeded(registeredUser);
        SavedChat newChat = new SavedChat(registeredUser);
        savedChatRepository.save(newChat);
        return newChat;
    }

    @Override
    public void freeSpaceIfNeeded(RegisteredUser registeredUser) {
        List<SavedChat> chats = savedChatRepository.findSavedChatsByUserUserId(registeredUser.getUserId());
        if (chats.size() >= savedChatsLimit) {
            SavedChat oldest = chats.stream()
                    .min(Comparator.comparing(Chat::getLastModifiedTs))
                    .orElseThrow();

            unlinkChatFromRegisteredUser(registeredUser, oldest);
        }
    }

    // will be used intern (in this class) and on registered user delete chat (we will delete from their side only)
    public void unlinkChatFromRegisteredUser(RegisteredUser registeredUser, SavedChat savedChat) {
        if (!savedChatRepository.existsByIdAndCreatedByUserId(savedChat.getId(), registeredUser.getUserId())) {
            throw new RuntimeException();
        }
        savedChat.setUser(null);
        savedChatRepository.save(savedChat);
    }

    // tmpChatService must implement this which will be true everytime
    @Override
    public boolean isChatLimitNotExceeded(RegisteredUser registeredUser) {
        Subscription current = subscriptionService.getActiveSubscription(registeredUser.getUserId());
        // search by createdBy because user is relational and can be unlinked
        return savedChatRepository.findSavedChatsByCreatedBy_UserIdAndStartTsAfter(registeredUser.getUserId(),
                LocalDateTime.now().minusDays(1)).size() < current.getPlan().getDayChatLimit();
    }

    @Override
    public Chat findById(Long chatId){
        return savedChatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
    }

    @Override
    public List<SavedChat> getChatsForCurrentUser() {
        RegisteredUser user = (RegisteredUser) userService.getCurrentUser();
        return savedChatRepository.findSavedChatsByUserUserId(user.getUserId());
    }

    @Override
    public void addPrompt(Chat chat, Prompt prompt, Request request, Response response) {

        prompt.getRequests().add(request);
        prompt.setChat(chat);

        requestRepository.save(request);
        responseRepository.save(response);
        promptRepository.save(prompt);
        savedChatRepository.save((SavedChat) chat);
    }
}
