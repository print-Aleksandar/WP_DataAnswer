package mk.wp.dataanswering.backend.service;

import org.springframework.web.multipart.MultipartFile;

import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.UploadedFile;

public interface  UploadFileService {

    UploadedFile saveFile(MultipartFile file, Chat chat) throws Exception;
}
