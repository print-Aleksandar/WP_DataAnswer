package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import mk.wp.dataanswering.backend.model.enums.ChatType;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    public Chat(ChatType chatType) {
        this.chatType = chatType;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @Column(nullable=true, length=255)
    private String chatName;

    @CreationTimestamp
    @Column(updatable=false)
    private LocalDateTime startTs;

    @UpdateTimestamp
    private LocalDateTime lastModifiedTs;

    @OneToOne(mappedBy="chat")
    private UploadedFile file;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "chat_type")
    @Enumerated(EnumType.STRING)
    private ChatType chatType;
}
