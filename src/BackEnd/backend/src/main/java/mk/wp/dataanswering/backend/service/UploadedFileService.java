package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.UploadedFile;
import java.util.List;

public interface UploadedFileService {

    UploadedFile storeFile(Long chatId, String fileName, String fileType, String minioKey);

    List<UploadedFile> findByChatId(Long chatId);

    UploadedFile findById(Long id);

    void deleteFile(Long id);
    
}
