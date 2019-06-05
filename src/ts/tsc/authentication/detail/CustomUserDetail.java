package ts.tsc.authentication.detail;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ts.tsc.authentication.entity.Role;
import ts.tsc.authentication.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CustomUserDetail implements UserDetails {

    private Collection<? extends GrantedAuthority> authorities;
    private String password;
    private String username;

    CustomUserDetail(User user) {
        this.password = user.getPassword();
        this.username = user.getName();
        this.authorities = translate(user.getRoles());
    }

    private Collection<? extends GrantedAuthority> translate(Set<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            String name = role.getRoleName().toUpperCase();
            if (!name.startsWith("ROLE_")) {
                name = "ROLE_" + name;
            }
            authorities.add(new SimpleGrantedAuthority(name));
        }
        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
