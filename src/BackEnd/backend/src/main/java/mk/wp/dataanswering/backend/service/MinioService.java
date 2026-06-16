package mk.wp.dataanswering.backend.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    
    String uploadFile(MultipartFile file, Long userId, Long chatId) throws Exception;
    InputStream downloadFile(String minioKey, Long userId, Long chatId) throws Exception; //Download file by minioKey
    void deleteFile(String minioKey, Long userId, Long chatId) throws Exception;

}
