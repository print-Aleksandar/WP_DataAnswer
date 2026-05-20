package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private final AuthUtils authUtils;
    private final TmpUserRepository tmpUserRepository;

    public UserServiceImp(AuthUtils authUtils, TmpUserRepository tmpUserRepository) {
        this.authUtils = authUtils;
        this.tmpUserRepository = tmpUserRepository;
    }

    @Override
    public TmpUser createTmpUser(String sessionId) {
        TmpUser tmpUser = new TmpUser();
        tmpUser.setSessionId(sessionId);
        tmpUserRepository.save(tmpUser);
        return tmpUser;
    }

    @Override
    public TmpUser getTmpUserBySession(String sessionId) {
        Optional<TmpUser> found = tmpUserRepository.findBySessionId(sessionId);
        return found.orElseGet(() -> createTmpUser(sessionId));
    }

    @Override
    public User getCurrentUser() {
        if (authUtils.isLoggedIn()) {
            return authUtils.getCurrentUser();
        } else {
            return getTmpUserBySession(authUtils.getTempSessionId());
        }
    }
}
