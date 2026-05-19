package mk.wp.dataanswering.backend.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("TMP")
public class TmpChat extends Chat {

    @ManyToOne
    @JoinColumn(name = "user_tmp_id", nullable = false)
    private UnRegisteredUser unregisteredUser;

}
