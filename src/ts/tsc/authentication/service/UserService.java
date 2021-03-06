package ts.tsc.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ts.tsc.authentication.entity.Role;
import ts.tsc.authentication.entity.RoleName;
import ts.tsc.authentication.entity.User;
import ts.tsc.authentication.repository.RoleRepository;
import ts.tsc.authentication.repository.UserRepository;
import ts.tsc.system.service.named.NamedService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ts.tsc.authentication.error.UserError.*;

@Service("userService")
@Transactional
public class UserService extends NamedService<User, Long> implements UserInterface {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       TokenStore tokenStore) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenStore = tokenStore;
    }

    @Override
    public UserRepository getRepository() {
        return this.userRepository;
    }

    @Override
    public User update(Long id, User user) {
        return userRepository.findById(id)
                .map(record -> {
                    record.setName(user.getName());
                    record.setPassword(user.getPassword());
                    record.setRoles(user.getRoles());
                    return userRepository.save(record);
                }).orElse(null);
    }



    @Override
    public boolean validateCreatingUser(User user) {
        Optional<User> userOptional = userRepository.findUserByName(user.getName());
        return !userOptional.isPresent();
    }

    @Override
    public User save(User user) {

        Optional<Role> roleOptional
                = roleRepository.findByRoleName(RoleName.USER.toString());
        if(!roleOptional.isPresent()) {
            return null;
        }
        try {
            Role role = roleOptional.get();
            role.addUser(user);
            roleRepository.save(role);
        } catch (Exception e) {
            return null;
        }
        user.addRole(roleOptional.get());
        return super.save(user);
    }

    @Override
    public int validateUpdatingPassword(User user, String oldPassword) {
        if(!(passwordEncoder.matches(oldPassword, user.getPassword()))) {
            return INVALID_PASSWORD.getCode();
        }
        return  SUCCEED.getCode();
    }

    @Override
    public Optional<User> findUserByName(String username) {
        return userRepository.findUserByName(username);
    }

    @Override
    public int revokeToken(String username) {
        try {

            @SuppressWarnings("all")
            Query typedQuery =
                    entityManager.createNativeQuery("select client_id from oauth_client_details");

            @SuppressWarnings("unchecked")
            List<String> clientIDList = typedQuery.getResultList();

            for(String clientID: clientIDList) {
                Collection<OAuth2AccessToken> oAuth2AccessTokenCollection
                        = tokenStore.findTokensByClientIdAndUserName(clientID, username);
                System.out.println(oAuth2AccessTokenCollection.size());
                for(OAuth2AccessToken oAuth2AccessToken : oAuth2AccessTokenCollection) {
                    tokenStore.removeRefreshToken(oAuth2AccessToken.getRefreshToken());
                    tokenStore.removeAccessToken(oAuth2AccessToken);
                }
            }

            return SUCCEED.getCode();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
            //return ERROR_WHILE_CHANGING_PASSWORD.getCode();
        }
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
