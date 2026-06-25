package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.Response;
import mk.wp.dataanswering.backend.model.ToolCall;
import mk.wp.dataanswering.backend.model.dto.ToolCallDto;

public interface ToolCallService {
    List<ToolCall> saveAllToResponse(List<ToolCallDto> toolDto, Response response );
}
