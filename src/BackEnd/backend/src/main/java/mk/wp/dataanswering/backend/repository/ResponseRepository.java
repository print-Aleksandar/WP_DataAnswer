package mk.wp.dataanswering.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Response;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    @Modifying
    @Query(value = """
    DELETE FROM responses
    WHERE prompt_id IN (
        SELECT p.prompt_id
        FROM prompts p
        JOIN tmp_chats tc ON p.chat_id = tc.chat_id
        WHERE tc.tmp_user_id = :tmpUserId
    )
    """, nativeQuery = true)
    void deleteResponsesByTmpUserId(@Param("tmpUserId") Long tmpUserId);

    @Query(value = """
    SELECT r.* FROM responses r
    WHERE r.prompt_id IN (
        SELECT p.prompt_id
        FROM prompts p
        JOIN tmp_chats tc ON p.chat_id = tc.chat_id
        WHERE tc.tmp_user_id = :userId
        UNION ALL
        SELECT p.prompt_id
        FROM prompts p
        JOIN saved_chats sc ON p.chat_id = sc.chat_id
        WHERE sc.created_by_user_id = :userId
    )
    """, nativeQuery = true)
    List<Response> findAllByUserId(@Param("userId") Long userId);
}
