package mk.wp.dataanswering.backend.repository;

import mk.wp.dataanswering.backend.model.TmpChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TmpChatRepository extends JpaRepository<TmpChat, Long> { ;
}
