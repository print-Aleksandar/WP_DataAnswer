package mk.wp.dataanswering.backend.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.service.impl.TmpUserLoggerService;
import org.springframework.beans.factory.annotation.Value;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Duration;

@Component
public class SessionTracker implements HttpSessionListener {

    private final TmpUserRepository tmpUserRepository;
    private final TmpUserLoggerService tmpUserLoggerService;

    public SessionTracker(TmpUserRepository tmpUserRepository,
                          TmpUserLoggerService tmpUserLoggerService) {
        this.tmpUserRepository = tmpUserRepository;
        this.tmpUserLoggerService = tmpUserLoggerService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {}

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        String sessionId = event.getSession().getId();
        tmpUserRepository.findBySessionId(sessionId)
                .ifPresent((tmpUser) -> {
                    tmpUserLoggerService.logSessionExpired(sessionId);
                    tmpUserRepository.delete(tmpUser);
                });
    }
}