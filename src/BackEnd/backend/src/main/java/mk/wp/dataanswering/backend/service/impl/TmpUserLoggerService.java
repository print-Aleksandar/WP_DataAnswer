package mk.wp.dataanswering.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TmpUserLoggerService {

    private final SubscriptionService subscriptionService;

    public void logCreated(TmpUser tmpUser) {
        log.info("TmpUser created — sessionId: {}, userId: {}",
                tmpUser.getSessionId(), tmpUser.getUserId());
    }

    public void logDeleted(String sessionId) {
        log.info("TmpUser deleted — sessionId: {}", sessionId);
    }

    public void logSessionExpired(String sessionId) {
        log.info("Session expired — sessionId: {}", sessionId);
    }

    public void logChatCreated(TmpUser tmpUser) {
        log.info("TmpChat created — userId: {}", tmpUser.getUserId());
    }

    public void logGuestPlanCreated(TmpUser tmpUser) {
        log.info("Guest plan created - userId: {}", subscriptionService.getActiveSubscription(tmpUser.getUserId()));
    }

    public void logChatDeleted(TmpUser tmpUser) {
        log.info("TmpChat deleted — userId: {}", tmpUser.getUserId());
    }

    public void logGuestPlanDeleted(TmpUser tmpUser) {
        log.info("Guest plan deleted - userId: {}", subscriptionService.getActiveSubscription(tmpUser.getUserId()));
    }
}
