package mk.wp.dataanswering.backend.service;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonParseException;
import mk.wp.dataanswering.backend.model.dto.LlmRequest;

public interface LlmService {
    public void streamPrompt(LlmRequest request, OutputStream outputStream) throws IOException, JsonParseException;
}
