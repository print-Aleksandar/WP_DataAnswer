package mk.wp.dataanswering.backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tmp_user_id")
    private TmpUser user;

}
