package mk.wp.dataanswering.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Response;
import mk.wp.dataanswering.backend.model.ToolCall;
import mk.wp.dataanswering.backend.model.dto.ToolCallDto;
import mk.wp.dataanswering.backend.repository.ToolCallRepository;
import mk.wp.dataanswering.backend.service.ToolCallService;

@Service
@AllArgsConstructor
public class ToolCallServiceImpl implements ToolCallService {

    private final ToolCallRepository toolCallRepository;

    @Override
    public List<ToolCall> saveAllToResponse(List<ToolCallDto> toolDto, Response response) {
        return toolCallRepository.saveAll(
            toolDto.stream().map(t -> 
                new ToolCall(t.id(), t.name(), response, t.response())
            ).toList()
        );
    }
    
}
