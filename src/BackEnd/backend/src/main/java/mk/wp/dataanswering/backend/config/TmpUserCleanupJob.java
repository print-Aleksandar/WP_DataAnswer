package mk.wp.dataanswering.backend.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.TmpUser;
import mk.wp.dataanswering.backend.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TmpUserCleanupJob implements ApplicationRunner {

    private final TmpUserRepository tmpUserRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final TmpChatRepository tmpChatRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        tmpChatRepository.deleteAll();
        tmpUserRepository.deleteAll();
        userRepository.deleteOrphanedUsers();
        chatRepository.deleteOrphanedChats();
    }
}