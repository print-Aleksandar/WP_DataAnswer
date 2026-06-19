package mk.wp.dataanswering.backend.service;

import org.springframework.web.multipart.MultipartFile;

import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.UploadedFile;
import mk.wp.dataanswering.backend.model.User;

public interface  UploadFileService {

    UploadedFile saveFile(MultipartFile file, User user, Chat chat) throws Exception;
    UploadedFile findByChat(Chat chat) throws Exception;
}
