package mk.wp.dataanswering.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.RegisteredUser;

@Repository
public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {
    
    Optional<RegisteredUser> findByUsername(String username);
    Optional<RegisteredUser> findByUsernameAndPassword(String username, String password);
}
