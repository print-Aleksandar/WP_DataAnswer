package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="subscriptions")
@AllArgsConstructor
@NoArgsConstructor
public class Subscription {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="subscription_id")
    private Long id;
    
    @Column(nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime startTs;

    @Column(nullable=false)
    private LocalDateTime endTs;

    // Tmp user has guest plan, changed to abstract
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="plan_id", nullable=false)
    private Plan plan;

    public void setRegisteredUser(User savedAdmin) {
        user = savedAdmin;
    }

    public void setPlan(Plan pro) {
        plan = pro;
    }

    public void setActive(boolean b) {
        isActive = b;
    }

    public void setEndTs(LocalDateTime EndTs) {
        endTs = EndTs;
    }
}
