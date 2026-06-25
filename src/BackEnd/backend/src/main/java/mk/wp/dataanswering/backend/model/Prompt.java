package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
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

    public void setPromptText(String promptText) {
        this.promptText = promptText;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    @OneToOne(mappedBy = "prompt")
    private Response response;


    public Prompt(String promptText, Chat chat){
        this.promptText = promptText;
        this.chat = chat;
        this.promptTs = LocalDateTime.now();
    }
}