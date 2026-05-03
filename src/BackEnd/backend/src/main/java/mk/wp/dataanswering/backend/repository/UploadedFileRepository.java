package mk.wp.dataanswering.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.UploadedFile;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    List<UploadedFile> findByMessageId(Long messageId);
    UploadedFile findByMinioKey(String minioKey);
}
