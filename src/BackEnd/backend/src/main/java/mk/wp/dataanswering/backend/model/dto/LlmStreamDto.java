package mk.wp.dataanswering.backend.model.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LlmStreamDto {
    List<ToolCallDto> toolCalls = new ArrayList<>();
    Long tokenUsage = -1l;
}
