package mk.wp.dataanswering.backend.service.impl;

import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.Plan;
import mk.wp.dataanswering.backend.model.Subscription;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.repository.RequestRepository;
import mk.wp.dataanswering.backend.service.RequestService;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final RequestRepository requestRepository;

    @Override
    public boolean isNotRequestLimitForChatExceeded(Chat chat) {
        User currentUser = userService.getCurrentUser();
        Subscription currentSubscription = subscriptionService.getActiveSubscription(currentUser.getUserId());
        Plan currentPlan = currentSubscription.getPlan();

        return requestRepository.getRequestsByPrompt_ChatId(chat.getId()).size()
                < currentPlan.getRequestPerChatLimit();
    }
}
