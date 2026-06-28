package mk.wp.dataanswering.backend.model.exceptions;

import java.time.LocalDateTime;

public class InChatTokenExceededException extends RuntimeException {
    private LocalDateTime limitTill;
    public InChatTokenExceededException(LocalDateTime limitTill) {
        super("In Chat Token Exceeded Exception.");
        this.limitTill = limitTill;
    }

    public LocalDateTime getLimitTill() { return this.limitTill; }
}
