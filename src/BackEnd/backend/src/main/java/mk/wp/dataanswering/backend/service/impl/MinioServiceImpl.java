package mk.wp.dataanswering.backend.service.impl;

import java.io.InputStream;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import mk.wp.dataanswering.backend.service.MinioService;

@Service
public class MinioServiceImpl implements MinioService {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    private MinioClient minioClient;

    @PostConstruct
    public void init() throws Exception{
        minioClient = MinioClient.builder()
                        .endpoint(url)
                        .credentials(accessKey, secretKey)
                        .build();

        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());

        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    @Override
    public String uploadFile(MultipartFile file, Long userId, Long chatId) throws Exception {
        String minioKey = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String path = "upload/" + userId + "/" + chatId + "/" + minioKey;

        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build()
        );
        return minioKey;
    }

    @Override
    public InputStream downloadFile(String minioKey, Long userId, Long chatId) throws Exception {
        String path = "upload/" + userId + "/" + chatId + "/" + minioKey;
        
        return minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build()
        );

    }

    @Override
    public void deleteFile(String minioKey, Long userId, Long chatId) throws Exception {
        String path = "upload/" + userId + "/" + chatId + "/" + minioKey;

        minioClient.removeObject(
            RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(path)
                .build()
        );
    }
    
}
