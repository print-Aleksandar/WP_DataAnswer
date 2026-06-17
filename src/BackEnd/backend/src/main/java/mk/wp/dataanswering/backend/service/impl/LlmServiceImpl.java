package mk.wp.dataanswering.backend.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import mk.wp.dataanswering.backend.model.dto.LlmRequest;
import mk.wp.dataanswering.backend.service.LlmService;

@Service
public class LlmServiceImpl implements LlmService {
    @Value("${services.llm.url}")
    private String baseUrl;

    private ObjectMapper objectMapper= new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public LlmServiceImpl() {
    }

    @Override
    public void streamPrompt(LlmRequest request, OutputStream outputStream) throws IOException, JsonParseException {
        String body = this.objectMapper.writeValueAsString(request);

        URI endpoint = URI.create(baseUrl + "/ask");

        RequestCallback requestCallback = clientRequest -> {
            clientRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            clientRequest.getBody().write(body.getBytes(StandardCharsets.UTF_8));
        };

        ResponseExtractor<Void> responseExtractor = res -> {
            if (res.getStatusCode() != HttpStatus.OK) {
                outputStream.flush();
                throw new IOException("LLM Service returned status " + res.getStatusCode());
            }
            
            try (InputStream bodyStream = res.getBody()) {
                byte[] buffer = new byte[512];
                int read;
                while( (read = bodyStream.read(buffer)) != -1 ){
                    outputStream.write(buffer, 0, read);
                    outputStream.flush(); // send chunks to client
                }
            }

            return null;
        };

        restTemplate.execute(endpoint, HttpMethod.POST, requestCallback, responseExtractor);
    }
}
