package mk.wp.dataanswering.backend.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import mk.wp.dataanswering.backend.model.dto.LlmRequest;
import mk.wp.dataanswering.backend.model.dto.LlmStreamDto;
import mk.wp.dataanswering.backend.model.dto.ToolCallDto;

public interface LlmService {
    public LlmStreamDto streamPrompt(LlmRequest request, OutputStream outputStream) throws IOException, JsonParseException;
    public String generateChatTitle(String promptText, String responseText);
}
