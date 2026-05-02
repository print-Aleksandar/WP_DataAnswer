package mk.wp.dataanswering.backend.repository;

import mk.wp.dataanswering.backend.model.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    List<UploadedFile> findByChatId(Long chatId);
}
