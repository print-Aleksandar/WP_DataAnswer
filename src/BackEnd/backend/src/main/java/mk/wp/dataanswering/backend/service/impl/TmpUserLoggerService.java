package mk.wp.dataanswering.backend.service.impl;

import lombok.extern.slf4j.Slf4j;
import mk.wp.dataanswering.backend.model.TmpUser;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TmpUserLoggerService {

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

    public void logChatDeleted(TmpUser tmpUser) {
        log.info("TmpChat deleted — userId: {}", tmpUser.getUserId());
    }
}
