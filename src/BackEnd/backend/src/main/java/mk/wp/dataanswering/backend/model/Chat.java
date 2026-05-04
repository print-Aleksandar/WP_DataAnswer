package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String chatName;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy="chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Message> messages;

    public Chat(Client client, String chatName) {
        this.client = client;
        this.chatName = chatName;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}

// CREATE TABLE chat(
//     id Serial PRIMARY KEY,
//     client_id INTEGER NOT NULL,
//     file_id INTEGER NOT NULL,
//     chat_name VARCHAR(255),
//     created_at TIMESTAMP DEFAULT now(),
//     updated_at   TIMESTAMP DEFAULT now(),
//     FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE ON UPDATE CASCADE
// );
