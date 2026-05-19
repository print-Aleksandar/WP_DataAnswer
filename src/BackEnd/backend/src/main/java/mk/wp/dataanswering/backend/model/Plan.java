package mk.wp.dataanswering.backend.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="plan_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String planName;

    @Column(nullable = false)
    private double planCost;

    @Column(nullable = false)
    private int dayChatLimit;
    
    @Column(nullable = false)
    private int dayPromptLimit;

    @OneToMany(mappedBy="plan")
    private List<Subscription> subscriptions;
}
