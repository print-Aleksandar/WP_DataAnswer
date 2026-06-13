package mk.wp.dataanswering.backend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.UploadedFile;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.repository.UploadedFileRepository;
import mk.wp.dataanswering.backend.service.MinioService;
import mk.wp.dataanswering.backend.service.UploadFileService;

@Service
@AllArgsConstructor
public class UploadFileServiceImpl implements UploadFileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final MinioService minioService;
    private final ChatRepository chatRepository;


    @Override
    public UploadedFile saveFile(MultipartFile file, Chat chat) throws Exception {
        String minioKey = minioService.uploadFile(file);

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setFileType(file.getContentType());
        uploadedFile.setMinioKey(minioKey);
        uploadedFile.setChat(chat);

        chat.setChatName("Answering " + file.getOriginalFilename()); // IME MOZE DA SE SMENI -> STAVENO E DA E ANSWERING DOCUMENT-NAME
        chatRepository.save(chat);

        return uploadedFileRepository.save(uploadedFile);
    }
    
}
