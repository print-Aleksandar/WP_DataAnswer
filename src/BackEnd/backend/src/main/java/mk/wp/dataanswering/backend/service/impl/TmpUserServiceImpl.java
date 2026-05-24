package mk.wp.dataanswering.backend.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.service.TmpUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TmpUserServiceImpl implements TmpUserService {

    @Value("${app.session.timeout-seconds}")
    private int sessionTimeout;

    private final TmpUserRepository tmpUserRepository;
    private final TmpUserLoggerService tmpUserLoggerService;

    @Override
    public TmpUser createTmpUser(HttpSession session) {
        TmpUser tmpUser = new TmpUser();
        tmpUser.setSessionId(session.getId());
        tmpUserRepository.save(tmpUser);
        session.setMaxInactiveInterval(sessionTimeout);
        tmpUserLoggerService.logCreated(tmpUser);
        return tmpUser;
    }

    @Override
    public TmpUser getTmpUserBySession(HttpSession session) {
        Optional<TmpUser> found = tmpUserRepository.findBySessionId(session.getId());
        return found.orElseGet(() -> createTmpUser(session));
    }
}
