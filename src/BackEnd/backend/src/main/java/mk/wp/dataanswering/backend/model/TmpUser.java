package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name="tmp_users")
@DiscriminatorValue("UNREGISTERED")
@AllArgsConstructor
@NoArgsConstructor
public class TmpUser extends User {

    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id")
    private TmpChat chat;
}
