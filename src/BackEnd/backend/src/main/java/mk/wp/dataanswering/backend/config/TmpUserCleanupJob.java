package mk.wp.dataanswering.backend.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.model.enums.ChatType;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.repository.RegisteredUserRepository;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TmpUserCleanupJob implements ApplicationRunner {

    private final TmpUserRepository tmpUserRepository;
    private final UserRepository userRepository;
    private final RegisteredUserRepository registeredUserRepository;
    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        chatRepository.deleteChatsByChatType(ChatType.TEMPORARY);
        tmpUserRepository.deleteAll();
        userRepository.deleteOrphanedUsers();
    }
}