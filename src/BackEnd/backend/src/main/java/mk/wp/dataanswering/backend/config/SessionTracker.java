package mk.wp.dataanswering.backend.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Value;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class SessionTracker implements HttpSessionListener {

    private final TmpUserRepository tmpUserRepository;
    private final ChatRepository chatRepository;

    @Value("${server.servlet.session.timeout}")
    private int sessionTimeout;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        event.getSession().setMaxInactiveInterval(sessionTimeout);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        String sessionId = event.getSession().getId();
        tmpUserRepository.findBySessionId(sessionId)
                .ifPresent(tmpUser -> {
                    if (tmpUser.getChat() != null) {
                        chatRepository.delete(tmpUser.getChat());
                    }
                    tmpUserRepository.delete(tmpUser);
                });
    }
}