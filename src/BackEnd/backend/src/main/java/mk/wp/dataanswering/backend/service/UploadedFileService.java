package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.UploadedFile;

public interface UploadedFileService {

    UploadedFile storeFile(Long messageId, String fileName, String fileType, String processType, String minioKey);

    List<UploadedFile> findByMessageId(Long messageId);

    UploadedFile findByMinioKey(String minioKey);
    
    void deleteFile(Long id);
    
}
