package mk.wp.dataanswering.backend.model.exceptions;

public class InChatTokenExceededException extends RuntimeException {
    public InChatTokenExceededException() {
        super("In Chat Token Exceeded Exception.");
    }
}
