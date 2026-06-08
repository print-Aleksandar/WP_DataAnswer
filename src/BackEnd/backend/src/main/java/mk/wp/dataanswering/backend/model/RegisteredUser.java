package mk.wp.dataanswering.backend.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.wp.dataanswering.backend.model.enums.Role;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name="registered_users")
@DiscriminatorValue("REGISTERED")
@AllArgsConstructor
@NoArgsConstructor
public class RegisteredUser extends User implements UserDetails {

    public RegisteredUser(String username,String userFirstName, String userEmail, String userLastName,String password, Role role) {
        this.username = username;
        this.userFirstName = userFirstName;
        this.userEmail = userEmail;
        this.userLastName = userLastName;
        this.password = password;
        this.role = role;
    }

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 50)
    private String userFirstName;

    @Column(unique = true, nullable = false, length = 150)
    private String userEmail;

    @Column(nullable = false, length = 100)
    private String userLastName;

    @Column(nullable = false, length = 200)
    private String password;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime registeredTs;

    @OneToMany(mappedBy = "user")
    private List<SavedChat> chats;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ColumnDefault("true")
    private boolean accountNonExpired = true;

    @ColumnDefault("true")
    private boolean accountNonLocked = true;

    @ColumnDefault("true")
    private boolean credentialsNonExpired = true;

    @ColumnDefault("true")
    private boolean enabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void softDelete() {
        username = "[DELETED_" + getUserId() + "]";
        userEmail = "[DELETED_" + getUserId() + "]";
        password = "[DELETED]";
        userFirstName = "[DELETED]";
        userLastName = "[DELETED]";
        enabled = false;
        credentialsNonExpired = false;
        accountNonLocked = false;
        accountNonExpired = false;
    }
}
