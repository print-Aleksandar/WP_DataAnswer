package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.config.AuthUtils;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.service.PlanService;
import mk.wp.dataanswering.backend.service.TmpUserService;
import mk.wp.dataanswering.backend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final AuthUtils authUtils;
    private final TmpUserService tmpUserService;

    public UserServiceImpl(AuthUtils authUtils,
                           TmpUserService tmpUserService) {
        this.authUtils = authUtils;
        this.tmpUserService = tmpUserService;
    }

    @Override
    public User getCurrentUser() {
        if (authUtils.isLoggedIn()) {
            return authUtils.getCurrentRegisteredUser();
        } else {
            return tmpUserService.getTmpUserBySession(authUtils.getCurrentSession());
        }
    }
}
