package mk.wp.dataanswering.backend.model.exceptions;

public class InvalidUserCredentialsException extends RuntimeException {
    
    public InvalidUserCredentialsException() {
        super("Invalid user credentials exception.");
    }
    
}
