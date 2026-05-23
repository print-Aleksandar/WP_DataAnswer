package mk.wp.dataanswering.backend.service.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties.Application;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import mk.wp.dataanswering.backend.model.dto.MultipartInputStreamFileResource;
import mk.wp.dataanswering.backend.service.ToolServiceInteractor;

@Service
public class RagInteractor implements ToolServiceInteractor {

    private final RestClient client;

    public RagInteractor(RestClient.Builder builder) {
        this.client = builder.baseUrl("http://localhost:7000").build();
    }

    @Override
    public void tryUpload(Long user_id, Long chat_id, MultipartFile file) {
        // throw new UnsupportedOperationException("Unimplemented method 'tryUpload'");

        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("user_id", user_id);
            body.add("chat_id", chat_id);
            body.add("file", new MultipartInputStreamFileResource(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getSize()
            ));
            
            this.client
            .post()
            .uri("/upload")
            .body(body)
            .retrieve()
            .toBodilessEntity();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file input stream", e);
        } catch (HttpClientErrorException | HttpServerErrorException e){
            return;
        }
    }

    @Override
    public List<String> getSupportedFileTypes() {
        return this.client
                .get()
                .uri("/upload")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<String>>() {})
                .getBody();
    }

    @Override
    public boolean healty() {
         try {
            client.get()
                .uri("/health")
                .retrieve()
                .toBodilessEntity();
                
            return true;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            return false;
        }

    }

    
}
