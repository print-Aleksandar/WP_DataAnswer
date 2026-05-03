package mk.wp.dataanswering.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.UploadedFile;
import mk.wp.dataanswering.backend.repository.UploadedFileRepository;
import mk.wp.dataanswering.backend.service.MessageService;
import mk.wp.dataanswering.backend.service.UploadedFileService;

@Service
@AllArgsConstructor
public class UploadedFileServiceImpl implements UploadedFileService {
    
    private final UploadedFileRepository uploadedFileRepository;
    private final MessageService messageService;
    
    @Override
    public UploadedFile storeFile(Long messageId, String fileName, String fileType, String processType,
            String minioKey) {
        if (messageId == null || fileName == null || fileType == null || processType == null || minioKey == null) {
            throw new IllegalArgumentException();
        }
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setMessage(messageService.findById(messageId));
        uploadedFile.setFileName(fileName);
        uploadedFile.setFileType(fileType);
        uploadedFile.setProcessType(processType);
        uploadedFile.setMinioKey(minioKey);
        return uploadedFileRepository.save(uploadedFile);
    }

    @Override
    public List<UploadedFile> findByMessageId(Long messageId) {
        return uploadedFileRepository.findByMessageId(messageId);
    }

    @Override
    public UploadedFile findByMinioKey(String minioKey) {
        return uploadedFileRepository.findByMinioKey(minioKey);
    }

    @Override
    public void deleteFile(Long id) {
        uploadedFileRepository.deleteById(id);
    }
    
}
