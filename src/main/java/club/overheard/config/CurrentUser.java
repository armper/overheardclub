package club.overheard.config;

import club.overheard.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

/** The authenticated principal: a thin wrapper around the User row. */
public record CurrentUser(User user) {
    public Long id() { return user.getId(); }
    public boolean isAdmin() { return user.isAdmin(); }
    public Collection<? extends GrantedAuthority> authorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
}
