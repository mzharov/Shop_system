package ts.tsc.system.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        try {
            auth.inMemoryAuthentication()
                    .withUser("user")
                    .password("{noop}password")
                    .roles("USER");
        } catch (Exception e) {
            logger.error("Не удалось сконфигурировать Web Security", e);
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/**").permitAll()
                .antMatchers("/shop/**").authenticated()
                .antMatchers("/product/**").authenticated()
                .antMatchers("/supplier/**").authenticated();
        http.csrf().disable();
        http.formLogin();
    }
}
