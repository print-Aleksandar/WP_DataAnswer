package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="prompts")
@AllArgsConstructor
@NoArgsConstructor
public class Prompt {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="prompt_id")
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime promptTs;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL)
    private List<Request> requests;
}
