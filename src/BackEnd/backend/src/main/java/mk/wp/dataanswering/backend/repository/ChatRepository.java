package mk.wp.dataanswering.backend.repository;

import mk.wp.dataanswering.backend.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByClientId(Long clientId);
}
