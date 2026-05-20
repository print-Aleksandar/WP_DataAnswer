package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import mk.wp.dataanswering.backend.model.enums.ChatType;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Chat {

    public Chat(User user, ChatType chatType) {
        this.user = user;
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

    @CreationTimestamp
    @Column(updatable=false)
    private LocalDateTime lastModifiedTs;

    @OneToMany(mappedBy="chat")
    private List<UploadedFile> files; // EDEN E!

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "chat_type")
    @Enumerated(EnumType.STRING)
    private ChatType chatType;
}
