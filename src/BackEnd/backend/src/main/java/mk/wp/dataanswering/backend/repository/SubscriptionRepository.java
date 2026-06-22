package mk.wp.dataanswering.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{

    void deleteAllByUser_UserId(Long userId);
    Optional<Subscription> findSubscriptionByUser_UserIdAndIsActiveTrue(Long userId);
    List<Subscription> findAllByUser_UserId(Long userUserId);

    @Modifying
    @Query(value = "DELETE FROM subscriptions WHERE user_id = :userId", nativeQuery = true)
    void deleteAllByUserIdNative(@Param("userId") Long userId);
}
