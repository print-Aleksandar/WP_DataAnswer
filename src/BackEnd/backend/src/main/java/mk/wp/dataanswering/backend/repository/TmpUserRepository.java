package mk.wp.dataanswering.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.TmpUser;

import java.util.Optional;

@Repository
public interface TmpUserRepository extends JpaRepository<TmpUser, Long> {

    Optional<TmpUser> findBySessionId(String sessionId);
    Optional<TmpUser> findByUserId(Long userId);
}
