package mk.wp.dataanswering.backend.service;

import mk.wp.dataanswering.backend.model.Chat;
import mk.wp.dataanswering.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatService<T extends Chat, R extends User, Y extends JpaRepository<T, Long> > {
    boolean supports();
    Chat startNewChat();
    void freeSpaceIfNeeded(R user);
    Y getCorrectChatRepository();
}
