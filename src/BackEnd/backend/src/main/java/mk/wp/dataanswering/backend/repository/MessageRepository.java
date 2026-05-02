package mk.wp.dataanswering.backend.repository;

import mk.wp.dataanswering.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatIdOrderBySequenceNoAsc(Long chatId);
}
