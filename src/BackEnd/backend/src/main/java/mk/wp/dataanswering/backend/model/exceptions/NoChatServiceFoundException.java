package mk.wp.dataanswering.backend.model.exceptions;

public class NoChatServiceFoundException extends RuntimeException {
    public NoChatServiceFoundException() {
        super("No chat service found exception.");
    }
}
