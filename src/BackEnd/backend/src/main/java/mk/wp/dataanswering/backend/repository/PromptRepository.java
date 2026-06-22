package mk.wp.dataanswering.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Prompt;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {
    List<Prompt> findByChatIdOrderByPromptTsAsc(Long chatId);
    List<Prompt> findAllByChatId(Long chatId);

    @Modifying
    @Query(value = "DELETE FROM prompts WHERE chat_id IN (SELECT chat_id FROM tmp_chats WHERE tmp_user_id = :userId)", nativeQuery = true)
    void deletePromptsByTmpUserId(@Param("userId") Long userId);
}
