package mk.wp.dataanswering.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.ToolCall;

@Repository
public interface ToolCallRepository extends JpaRepository<ToolCall, String> {

    @Modifying
    @Query(value = """
    DELETE FROM tool_call
    WHERE tool_call_id IN (
        SELECT t.tool_call_id
        FROM tool_call t
        JOIN responses r ON t.response_id = r.response_id
        JOIN prompts p ON p.prompt_id = r.prompt_id
        JOIN tmp_chats tc ON tc.chat_id = p.chat_id
        WHERE tc.tmp_user_id = :tmpUserId
    )
    """, nativeQuery = true)
    void deleteToolCallsByTmpUserId(@Param("tmpUserId") Long tmpUserId);
}
