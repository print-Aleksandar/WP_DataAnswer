package mk.wp.dataanswering.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.User;

import java.util.Collection;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = """
        DELETE FROM users 
        WHERE user_id NOT IN (SELECT user_id FROM registered_user)
        AND user_id NOT IN (SELECT user_id FROM tmp_user)
        """, nativeQuery = true)
    void deleteOrphanedUsers();
}
