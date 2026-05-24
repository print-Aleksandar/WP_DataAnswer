package mk.wp.dataanswering.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import mk.wp.dataanswering.backend.model.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        DELETE FROM chats 
        WHERE chat_id NOT IN (SELECT chat_id FROM saved_chats)
        AND chat_id NOT IN (SELECT chat_id FROM tmp_chats)
        """, nativeQuery = true)
    void deleteOrphanedChats();
}
