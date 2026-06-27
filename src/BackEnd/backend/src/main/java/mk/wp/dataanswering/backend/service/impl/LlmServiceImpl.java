package mk.wp.dataanswering.backend.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mk.wp.dataanswering.backend.model.dto.LlmRequest;
import mk.wp.dataanswering.backend.model.dto.LlmStreamDto;
import mk.wp.dataanswering.backend.model.dto.ToolCallDto;
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
    public LlmStreamDto streamPrompt(LlmRequest request, OutputStream outputStream) throws IOException, JsonParseException {
        String body = this.objectMapper.writeValueAsString(request);

        URI endpoint = URI.create(baseUrl + "/ask");

        Map<String, String> idToNameMap = new HashMap<>(); 

        LlmStreamDto result = new LlmStreamDto();

        RequestCallback requestCallback = clientRequest -> {
            clientRequest.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            clientRequest.getBody().write(body.getBytes(StandardCharsets.UTF_8));
        };

        ResponseExtractor<Void> responseExtractor = res -> {
            if (res.getStatusCode() != HttpStatus.OK) {
                // outputStream.flush();
                throw new IOException("LLM Service returned status " + res.getStatusCode());
            }
            
            try (InputStream bodyStream = res.getBody()) {
                JsonParser parser = objectMapper.getFactory().createParser(bodyStream);
                MappingIterator<JsonNode> chunks = objectMapper.readValues(parser, JsonNode.class);

                while (chunks.hasNext()) {
                    handleChunk(chunks.next(), outputStream, idToNameMap, result);
                }
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                outputStream.flush();
                throw new IOException("Streaming failed with reason: " + e.getMessage());
            } finally {
                outputStream.flush();
            }

            return null;
        };

        restTemplate.execute(endpoint, HttpMethod.POST, requestCallback, responseExtractor);

        return result;
    }

    private void handleChunk(JsonNode node, OutputStream out, Map<String, String> pending, LlmStreamDto result) throws IOException {

        if (node.has("token")) {
            // Send to frontend
            out.write((node.toString()+ "\n").getBytes(StandardCharsets.UTF_8));
            out.flush();

        } else if (node.has("tool_call")){
            // Add pending tool call
            String toolId = node.get("tool_id").asText();
            String toolName = node.get("tool_call").asText();
            pending.put(toolId, toolName);

            // send update to frontend
            out.write((node.toString()+ "\n").getBytes(StandardCharsets.UTF_8));
            out.flush();

        } else if (node.has("tool_response")) {
            // Convert to Dto
            String toolId = node.get("tool_id").asText();
            String tool_name = pending.remove(toolId);
            result.getToolCalls()
            .add(new ToolCallDto(
                toolId,
                tool_name,
                node.get("tool_response").asText()
                )
            );

            ObjectNode response = JsonNodeFactory.instance.objectNode();
            response.put("tool_response", tool_name);
            response.put("tool_id", toolId);

            out.write((response.toString() + '\n').getBytes(StandardCharsets.UTF_8));
            out.flush();


        } else if (node.has("token_usage")) {
            Long usage = node.get("token_usage").asLong();
            result.setTokenUsage(usage);
        }
    }
}
