package ts.tsc.authentication.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ts.tsc.system.entity.parent.NamedEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "_user")
public class User extends NamedEntity<Long> implements Serializable {

    @Column(name = "password")
    @JsonIgnore
    private String password;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;


    public void addRole(Role role) {
        if(roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
