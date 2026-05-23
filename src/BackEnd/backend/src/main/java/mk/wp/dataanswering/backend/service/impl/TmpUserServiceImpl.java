package mk.wp.dataanswering.backend.service.impl;

import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.service.TmpUserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TmpUserServiceImpl implements TmpUserService {

    private final TmpUserRepository tmpUserRepository;

    public TmpUserServiceImpl(TmpUserRepository tmpUserRepository) {
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
}
