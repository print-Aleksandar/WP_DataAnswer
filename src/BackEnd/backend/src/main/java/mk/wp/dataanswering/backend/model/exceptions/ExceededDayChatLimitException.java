package mk.wp.dataanswering.backend.model.exceptions;

public class ExceededDayChatLimitException extends RuntimeException {
    public ExceededDayChatLimitException() {
        super("Exceeded day chat limit exception.");
    }
}
