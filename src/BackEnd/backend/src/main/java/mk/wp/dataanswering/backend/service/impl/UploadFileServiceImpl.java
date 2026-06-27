package mk.wp.dataanswering.backend.service.impl;

import java.io.FileNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.UploadedFile;
import mk.wp.dataanswering.backend.model.User;
import mk.wp.dataanswering.backend.repository.ChatRepository;
import mk.wp.dataanswering.backend.repository.UploadedFileRepository;
import mk.wp.dataanswering.backend.service.ExternalToolService;
import mk.wp.dataanswering.backend.service.MinioService;
import mk.wp.dataanswering.backend.service.UploadFileService;

@Service
@AllArgsConstructor
public class UploadFileServiceImpl implements UploadFileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final MinioService minioService;
    private final ChatRepository chatRepository;
    private final ExternalToolService externalToolService;

    @Override
    public UploadedFile saveFile(MultipartFile file, User user, Chat chat) throws Exception {
        String minioKey = minioService.uploadFile(file, user.getUserId(), chat.getId());

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setFileType(file.getContentType());
        uploadedFile.setMinioKey(minioKey);
        uploadedFile.setChat(chat);

        //chat.setChatName("Answering " + file.getOriginalFilename()); // IME MOZE DA SE SMENI -> STAVENO E DA E ANSWERING DOCUMENT-NAME
        // chatRepository.save(chat);

        externalToolService.tryUploadToAll(
            user.getUserId(), 
            chat.getId(),
            file
        );

        return uploadedFileRepository.save(uploadedFile);
    }

    @Override
    public UploadedFile findByChat(Chat chat) throws Exception {

        return uploadedFileRepository.findByChat(chat)
        .orElseThrow(() -> new FileNotFoundException("File not fount!"));
    }
    
}
