package mk.wp.dataanswering.backend.repository;

import mk.wp.dataanswering.backend.model.TmpChat;
import mk.wp.dataanswering.backend.model.TmpUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TmpChatRepository extends JpaRepository<TmpChat, Long> {

    Optional<TmpChat> findByUser(TmpUser tmpUser);

    @Modifying
    @Query("DELETE FROM TmpChat t WHERE t.user.userId = :userId")
    void deleteByTmpUserId(@Param("userId") Long userId);
}
