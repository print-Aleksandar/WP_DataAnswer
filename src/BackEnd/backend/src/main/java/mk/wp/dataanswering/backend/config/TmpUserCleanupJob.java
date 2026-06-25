package mk.wp.dataanswering.backend.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.repository.*;
import mk.wp.dataanswering.backend.service.OrphanCleanupService;
import mk.wp.dataanswering.backend.service.TmpUserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TmpUserCleanupJob implements ApplicationRunner {

    private final TmpUserRepository tmpUserRepository;
    private final TmpUserService tmpUserService;
    private final OrphanCleanupService orphanCleanupService;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        tmpUserRepository.findAll()
                .forEach(u -> tmpUserService.cleanUpBeforeUserDeletion(u.getUserId()));

        em.flush();
        em.clear();

        orphanCleanupService.cleanupOrphans();
    }
}