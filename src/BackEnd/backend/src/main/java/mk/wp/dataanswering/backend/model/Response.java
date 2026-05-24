package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="responses")
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long id;

    @Column(nullable = false)
    private boolean isAnswerable = false;

    @Column(nullable = false)
    private boolean isAnswered = false;

    @Column(nullable = false)
    private boolean isCorrupted = false;

    @Column(columnDefinition = "TEXT")
    private String responseText;

    @OneToOne
    @JoinColumn(name = "request_id", nullable = false, unique = true)
    private Request request;
}
