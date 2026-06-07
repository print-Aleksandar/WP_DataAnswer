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
public class SavedChat extends Chat {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private RegisteredUser user;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)
    private RegisteredUser createdBy;

    public SavedChat(RegisteredUser user) {
        this.user = user;
        this.createdBy = user;
    }
}
