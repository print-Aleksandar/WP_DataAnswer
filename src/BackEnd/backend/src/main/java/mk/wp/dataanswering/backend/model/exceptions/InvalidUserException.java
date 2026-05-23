package mk.wp.dataanswering.backend.model.exceptions;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException() {
        super("Invalid type of user exception.");
    }
}
