package mk.wp.dataanswering.backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.repository.TmpChatRepository;
import mk.wp.dataanswering.backend.repository.TmpUserRepository;
import mk.wp.dataanswering.backend.repository.UserRepository;
import mk.wp.dataanswering.backend.service.OrphanCleanupService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrphanCleanupServiceImpl implements OrphanCleanupService {

        private final TmpChatRepository tmpChatRepository;
        private final TmpUserRepository tmpUserRepository;
        private final UserRepository userRepository;
        private final ChatRepository chatRepository;

        @Transactional
        public void cleanupOrphans() {
            tmpChatRepository.deleteAll();
            tmpUserRepository.deleteAll();
            userRepository.deleteOrphanedUsers();
            chatRepository.deleteOrphanedChats();
        }
}
