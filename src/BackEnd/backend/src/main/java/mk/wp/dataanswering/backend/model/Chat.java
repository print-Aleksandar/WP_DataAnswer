package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
