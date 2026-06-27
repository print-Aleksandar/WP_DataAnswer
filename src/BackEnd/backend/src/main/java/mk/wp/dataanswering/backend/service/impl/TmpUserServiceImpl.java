package mk.wp.dataanswering.backend.service.impl;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.Plan;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.repository.*;
import mk.wp.dataanswering.backend.service.PlanService;
import mk.wp.dataanswering.backend.service.SubscriptionService;
import mk.wp.dataanswering.backend.service.TmpUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TmpUserServiceImpl implements TmpUserService {

    private final ToolCallRepository toolCallRepository;
    @Value("${app.session.timeout-seconds}")
    private int sessionTimeout;

    private final TmpUserRepository tmpUserRepository;
    private final TmpUserLoggerService tmpUserLoggerService;
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanService planService;
    private final ResponseRepository responseRepository;
    private final TmpChatRepository tmpChatRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final PromptRepository promptRepository;

    @Override
    public TmpUser createTmpUser(HttpSession session) {
        TmpUser tmpUser = new TmpUser();
        tmpUser.setSessionId(session.getId());
        tmpUserRepository.save(tmpUser);
        session.setMaxInactiveInterval(sessionTimeout);
        tmpUserLoggerService.logCreated(tmpUser);
        Long planId = planService.findByPlanName("GUEST").getId();
        subscriptionService.subscribe(tmpUser.getUserId(), planId);
        return tmpUser;
    }

    @Override
    public TmpUser getTmpUserBySession(HttpSession session) {
        Optional<TmpUser> found = tmpUserRepository.findBySessionId(session.getId());
        return found.orElseGet(() -> createTmpUser(session));
    }

    @Override
    @Transactional
    public void cleanUpBeforeUserDeletion(long userId) {
        if (tmpUserRepository.findByUserId(userId).isPresent()) {
            toolCallRepository.deleteToolCallsByTmpUserId(userId);
            responseRepository.deleteResponsesByTmpUserId(userId);
            promptRepository.deletePromptsByTmpUserId(userId);
            uploadedFileRepository.deleteByTmpUserId(userId);
            tmpChatRepository.deleteByTmpUserId(userId);
            subscriptionRepository.deleteAllByUserIdNative(userId);
        }
    }
}
