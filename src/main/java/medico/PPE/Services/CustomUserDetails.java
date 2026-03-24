package medico.PPE.Services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final String username;
    private final String password;
    private final boolean enabled;
    private final Long id;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, boolean enabled,
                             Long id, Collection<? extends GrantedAuthority> authorities) {
        this.username    = username;
        this.password    = password;
        this.enabled     = enabled;
        this.id          = id;
        this.authorities = authorities;
    }

    public Long getId() { return id; }

    @Override public String getUsername()     { return username; }
    @Override public String getPassword()     { return password; }
    @Override public boolean isEnabled()      { return enabled; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}