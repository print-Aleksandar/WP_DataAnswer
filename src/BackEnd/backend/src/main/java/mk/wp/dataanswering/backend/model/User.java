package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name="users")
@Inheritance(strategy=InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @OneToMany(mappedBy="user", fetch=FetchType.LAZY)
    private List<Subscription> subscriptions;

    @Column
    private LocalDateTime limitTill = null;
}
