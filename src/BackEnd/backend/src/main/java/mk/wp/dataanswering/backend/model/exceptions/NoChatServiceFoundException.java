package mk.wp.dataanswering.backend.model.exceptions;

public class NoChatServiceFoundException extends RuntimeException {
  public NoChatServiceFoundException(String message) {
    super(message);
  }
}
