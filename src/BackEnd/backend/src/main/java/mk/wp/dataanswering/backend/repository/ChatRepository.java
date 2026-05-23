package mk.wp.dataanswering.backend.repository;

import jakarta.transaction.Transactional;
import mk.wp.dataanswering.backend.model.enums.ChatType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long>{
    @Modifying
    @Query("DELETE FROM Chat c WHERE c.chatType = :chatType")
    void deleteChatsByChatType(@Param("chatType") ChatType chatType);
}
