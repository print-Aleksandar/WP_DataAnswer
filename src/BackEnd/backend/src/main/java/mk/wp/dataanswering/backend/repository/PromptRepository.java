package mk.wp.dataanswering.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mk.wp.dataanswering.backend.model.Prompt;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, Long> {
    
}
