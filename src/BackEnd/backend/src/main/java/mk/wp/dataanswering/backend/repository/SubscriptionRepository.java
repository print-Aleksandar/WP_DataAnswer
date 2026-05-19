package mk.wp.dataanswering.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{
    
    Optional<Subscription> findByRegisteredUser_UserIdAndIsActiveTrue(Long userId);

}
