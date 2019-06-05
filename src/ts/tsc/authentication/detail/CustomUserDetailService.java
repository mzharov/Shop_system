package ts.tsc.authentication.detail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ts.tsc.authentication.entity.User;
import ts.tsc.authentication.repository.UserRepository;

import java.util.Optional;

@Service("userDetailService")
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByName(name);
        if(!userOptional.isPresent()){
            throw new UsernameNotFoundException("UserName "+name+" not found");
        }
        return new CustomUserDetail(userOptional.get());
    }
}
