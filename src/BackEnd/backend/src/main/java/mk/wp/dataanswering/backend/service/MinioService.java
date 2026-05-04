package mk.wp.dataanswering.backend.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    
    String uploadFile(MultipartFile file) throws Exception;
    InputStream downloadFile(String minioKey) throws Exception; //Download file by minioKey
    void deleteFile(String minioKey) throws Exception;

}
