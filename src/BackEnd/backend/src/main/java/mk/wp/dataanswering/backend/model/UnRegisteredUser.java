package mk.wp.dataanswering.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@DiscriminatorValue("UNREGISTERED")
@AllArgsConstructor
@NoArgsConstructor
public class UnRegisteredUser extends User {
    
    @Column(name = "user_tmp_id",nullable = false, unique = true, length = 100)    
    private String UserTmpId;

}
