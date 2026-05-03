package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private int sequenceNo = 0;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    private Chat chat;
    
    @OneToMany(mappedBy = "message")
    private List<UploadedFile> files;

}
// CREATE TABLE message(
//     id Serial PRIMARY KEY,
//     chat_id INTEGER NOT NULL,
//     sequence_no INT NOT NULL, 
//     question TEXT NOT NULL, 
//     answer TEXT NOT NULL, 
//     created_at TIMESTAMP DEFAULT now(),
//     FOREIGN KEY (chat_id) REFERENCES chat(id) ON DELETE CASCADE ON UPDATE CASCADE,
//     CONSTRAINT chk_question CHECK (LENGTH(question) > 0),
//     CONSTRAINT chk_answer CHECK (LENGTH(answer) > 0)
// );