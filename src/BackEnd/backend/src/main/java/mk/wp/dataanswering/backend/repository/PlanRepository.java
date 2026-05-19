package mk.wp.dataanswering.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
}
