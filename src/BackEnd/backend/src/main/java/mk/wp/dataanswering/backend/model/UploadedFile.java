package mk.wp.dataanswering.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255)
    private String fileName;
    @Column(nullable = false, length = 100)
    private String fileType;
    @Column(nullable = false, length = 20)
    private String processType;
    @Column(nullable = false, length = 500)
    private String minIOKey;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

}
// CREATE TABLE uploaded_file(
//     id Serial PRIMARY KEY,
//     chat_id INTEGER NOT NULL,
//     file_name VARCHAR(255) NOT NULL,
//     file_type VARCHAR(100) NOT NULL,
//     process_type  VARCHAR(20) NOT NULL, -- 'rag' or 'structured'
//     minio_key     VARCHAR(500) NOT NULL, -- minIO key for file storage
//     uploaded_at TIMESTAMP DEFAULT now(),
//     FOREIGN KEY (chat_id) REFERENCES chat(id) ON DELETE CASCADE ON UPDATE CASCADE
// );