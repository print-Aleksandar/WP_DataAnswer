package mk.wp.dataanswering.backend.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.service.TmpUserService;
import mk.wp.dataanswering.backend.service.impl.TmpUserLoggerService;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionTracker implements HttpSessionListener {

    private final TmpUserRepository tmpUserRepository;
    private final TmpUserLoggerService tmpUserLoggerService;
    private final TmpUserService tmpUserService;

    @Override
    public void sessionCreated(HttpSessionEvent event) {}

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        String sessionId = event.getSession().getId();
        tmpUserRepository.findBySessionId(sessionId)
                .ifPresent((tmpUser) -> {
                    tmpUserLoggerService.logSessionExpired(sessionId);
                    tmpUserService.cleanUpBeforeUserDeletion(tmpUser.getUserId());
                    tmpUserRepository.deleteById(tmpUser.getUserId());
                });
    }
}