package mk.wp.dataanswering.backend.model.dto;

public record PromptRequest(
    String promptText,
    Long chatId
) {}