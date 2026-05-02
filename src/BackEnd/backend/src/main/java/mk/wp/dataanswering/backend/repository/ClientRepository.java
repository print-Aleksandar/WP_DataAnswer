package mk.wp.dataanswering.backend.repository;

import mk.wp.dataanswering.backend.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Optional<Client> findByUsername(String username);

    Optional<Client> findByUsernameAndPassword(String username, String password);

}
