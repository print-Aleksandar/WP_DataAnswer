package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Data
@Table(name="chats")
@Inheritance(strategy=InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
public abstract class Chat {

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
}
