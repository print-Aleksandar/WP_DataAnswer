package mk.wp.dataanswering.backend.repository;

import mk.wp.dataanswering.backend.model.SavedChat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedChatRepository extends JpaRepository<SavedChat, Long> {

    List<SavedChat> findSavedChatsByUserUserId(Long id);
}
