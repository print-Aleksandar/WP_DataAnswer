package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
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

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private RegisteredUser registeredUser;

    @ManyToOne
    @JoinColumn(name="plan_id", nullable=false)
    private Plan plan;

    public void setRegisteredUser(RegisteredUser savedAdmin) {
        registeredUser = savedAdmin;
    }

    public void setPlan(Plan pro) {
        plan = pro;
    }

    public void setActive(boolean b) {
        isActive = b;
    }

    public void setEndTs(LocalDateTime plusYears) {
        endTs = plusYears;
    }


}
