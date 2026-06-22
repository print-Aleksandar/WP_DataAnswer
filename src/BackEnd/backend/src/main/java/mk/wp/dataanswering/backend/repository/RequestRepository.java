package mk.wp.dataanswering.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> getRequestsByPrompt_ChatId(Long chatId);

    @Modifying
    @Query(value = "DELETE FROM requests WHERE prompt_id IN (SELECT p.prompt_id FROM prompts p JOIN tmp_chats tc ON p.chat_id = tc.chat_id WHERE tc.tmp_user_id = :userId)", nativeQuery = true)
    void deleteRequestsByTmpUserId(@Param("userId") Long userId);
}
