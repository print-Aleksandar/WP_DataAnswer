package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name="saved_chats")
@DiscriminatorValue("SAVED")
@AllArgsConstructor
@NoArgsConstructor
public class SavedChat extends Chat{

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RegisteredUser user;
}
