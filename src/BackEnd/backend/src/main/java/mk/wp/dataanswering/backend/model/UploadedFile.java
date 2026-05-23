package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="file_id")
    private Long id;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String fileType;

    @Column(name = "minio_key",nullable = false, length = 500)
    private String minioKey;

    @Column(name = "file_url", nullable = true, length = 500)
    private String fileUrl;

    @OneToOne
    @JoinColumn(name="chat_id", nullable=true)
    private Chat chat;

}