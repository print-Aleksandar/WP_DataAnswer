package mk.wp.dataanswering.backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mk.wp.dataanswering.backend.model.Plan;
import mk.wp.dataanswering.backend.repository.PlanRepository;
import mk.wp.dataanswering.backend.service.PlanService;

@Service
@AllArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public List<Plan> listAll() {
        return planRepository.findAll();
    }

    @Override
    public Plan findById(Long planId) {
        return planRepository.findById(planId).orElseThrow(()-> new RuntimeException("Plan not found with id: " + planId));
    }

    @Override
    public Plan create(String planName, Double planMonthlyCost, Integer dailyTokens) {
        Plan plan = new Plan();
        plan.setPlanName(planName);
        plan.setPlanMonthlyCost(planMonthlyCost);
        plan.setTokens(dailyTokens);
        return planRepository.save(plan);
    }

    @Override
    public Plan findByPlanName(String planName) {
        return planRepository.findByPlanName(planName).orElseThrow(()-> new RuntimeException("Plan Name not found" + planName));
    }
    
    

}
