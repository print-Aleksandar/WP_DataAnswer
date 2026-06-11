package mk.wp.dataanswering.backend.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import mk.wp.dataanswering.backend.service.ToolServiceInteractor;

@Service
public class RagInteractor implements ToolServiceInteractor {
    @Value("${services.rag.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new  RestTemplate();

    public RagInteractor() { }

    @Override
    public void tryUpload(Long user_id, Long chat_id, MultipartFile file) throws Exception {        
        byte[] fileBytes = file.getBytes();
        String originalFilename = file.getOriginalFilename();

        MultiValueMap<String, Object> bodyContent = new LinkedMultiValueMap<>();

        ByteArrayResource contentsAsResource = new ByteArrayResource(fileBytes){
            @Override
            public String getFilename(){
                return originalFilename == null ? "upload.pdf" : originalFilename;
            }
        };

        bodyContent.add("file", contentsAsResource);

        String fullUrl = this.baseUrl + "/upload/" + user_id + '/' + chat_id;

        this.restTemplate.postForObject(fullUrl, bodyContent, Void.class);
    }

    @Override
    public List<String> getSupportedFileTypes() {
        String[] res = this.restTemplate.getForObject(
            baseUrl + "/upload", 
            String[].class
        );

        return List.of(res);
        
    }

    @Override
    public boolean healty() {
        try {
            return this.restTemplate.getForEntity(
                baseUrl + "/healthy", 
                Void.class
            ).getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }

    }

    
}
