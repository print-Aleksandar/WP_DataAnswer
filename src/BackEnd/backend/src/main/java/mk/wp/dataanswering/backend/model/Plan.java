package mk.wp.dataanswering.backend.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="plans")
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
    private double planMonthlyCost;

    @Column(nullable = false)
    private int tokens;

    public Plan(String planName, double planMonthlyCost, int tokens) {
        this.planName = planName;
        this.planMonthlyCost = planMonthlyCost;
        this.tokens = tokens;
    }

    @OneToMany(mappedBy="plan")
    private List<Subscription> subscriptions;
}
