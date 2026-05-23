package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.service.TmpUserService;
import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private final AuthUtils authUtils;
    private final TmpUserRepository tmpUserRepository;
    private final TmpUserService tmpUserService;

    public UserServiceImp(AuthUtils authUtils,
                          TmpUserRepository tmpUserRepository,
                          TmpUserService tmpUserService) {
        this.authUtils = authUtils;
        this.tmpUserRepository = tmpUserRepository;
        this.tmpUserService = tmpUserService;
    }

    @Override
    public User getCurrentUser() {
        if (authUtils.isLoggedIn()) {
            return authUtils.getCurrentRegisteredUser();
        } else {
            return tmpUserService.getTmpUserBySession(authUtils.getTempSessionId());
        }
    }
}
