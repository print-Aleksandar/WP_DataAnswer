package mk.wp.dataanswering.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name="tmp_chats")
@DiscriminatorValue("TMP")
@AllArgsConstructor
@NoArgsConstructor
public class TmpChat extends Chat {

    @OneToOne(mappedBy = "chat")
    private TmpUser user;
}
