package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @Column(nullable=false, columnDefinition= "TEXT")
    private String promptText;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @OneToMany(mappedBy = "prompt", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Request> requests;

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
}
