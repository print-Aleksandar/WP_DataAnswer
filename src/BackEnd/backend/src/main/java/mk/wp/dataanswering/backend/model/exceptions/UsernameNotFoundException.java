package mk.wp.dataanswering.backend.model.exceptions;

public class UsernameNotFoundException extends RuntimeException {
    
    public UsernameNotFoundException(String username){
        super(String.format("User with username: %s not found", username));
    }

}
