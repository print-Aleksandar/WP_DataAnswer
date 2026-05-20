package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@DiscriminatorValue("UNREGISTERED")
@AllArgsConstructor
@NoArgsConstructor
public class TmpUser extends User {
    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    @OneToOne(mappedBy = "user")
    private Chat chat;
}
