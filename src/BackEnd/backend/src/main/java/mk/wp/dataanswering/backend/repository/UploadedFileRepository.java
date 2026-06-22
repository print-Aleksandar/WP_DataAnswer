package mk.wp.dataanswering.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.UploadedFile;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    Optional<UploadedFile> findByChat(Chat chat);

    @Modifying
    @Query(value = "DELETE FROM uploaded_files WHERE chat_id IN (SELECT chat_id FROM tmp_chats WHERE tmp_user_id = :userId)", nativeQuery = true)
    void deleteByTmpUserId(@Param("userId") Long userId);
}
