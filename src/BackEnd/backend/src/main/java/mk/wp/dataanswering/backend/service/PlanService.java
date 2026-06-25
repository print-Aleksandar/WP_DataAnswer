package mk.wp.dataanswering.backend.service;

import java.util.List;

import mk.wp.dataanswering.backend.model.Plan;

public interface PlanService {
    
    List<Plan> listAll();
    Plan findById(Long planId);

    Plan create(String planName, Double planMonthlyCost, Integer dailyTokens);

    Plan findByPlanName(String planName);
}
