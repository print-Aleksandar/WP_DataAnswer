package mk.wp.dataanswering.backend.repository;

import mk.wp.dataanswering.backend.model.RegisteredUser;
import mk.wp.dataanswering.backend.model.SavedChat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SavedChatRepository extends JpaRepository<SavedChat, Long> {

    // context when we are modeling what user sees
    List<SavedChat> findSavedChatsByUserUserId(Long id);

    // context when we are modeling system
    List<SavedChat> findSavedChatsByCreatedBy_UserIdAndStartTsAfter(Long id, LocalDateTime localDateTime);
    boolean existsByIdAndCreatedByUserId(Long id, Long createdByUserId);
}
