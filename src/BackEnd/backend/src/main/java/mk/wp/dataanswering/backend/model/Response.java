package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="responses")
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long id;

    @Column(nullable = false)
    private boolean isStopped = false;

    @Column(columnDefinition = "TEXT")
    private String responseText;

    @OneToOne
    @JoinColumn(name = "prompt_id", nullable = false)
    private Prompt prompt;
}
