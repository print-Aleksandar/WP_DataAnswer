package mk.wp.dataanswering.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getRequestsByPrompt_ChatId(Long chatId);
}
