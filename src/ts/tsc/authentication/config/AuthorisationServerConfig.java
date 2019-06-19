package ts.tsc.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorisationServerConfig
        extends AuthorizationServerConfigurerAdapter {

    @Value("${security.token.realm}")
    private String realm;

    private final TokenStore tokenStore;
    private final JwtAccessTokenConverter accessTokenConverter;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final DataSource dataSource;
    private final TokenEnhancer tokenEnhancer;


    @Autowired
    public AuthorisationServerConfig(TokenStore tokenStore,
                                     JwtAccessTokenConverter accessTokenConverter,
                                     @Qualifier("authenticationManagerBean")
                                                 AuthenticationManager authenticationManager,
                                     @Qualifier("userDetailService")
                                                 UserDetailsService userDetailsService,
                                     DataSource dataSource, TokenEnhancer tokenEnhancer) {
        this.tokenStore = tokenStore;
        this.accessTokenConverter = accessTokenConverter;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.dataSource = dataSource;
        this.tokenEnhancer = tokenEnhancer;
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(
                Arrays.asList(tokenEnhancer, accessTokenConverter));

        endpoints.tokenStore(tokenStore)
                .accessTokenConverter(accessTokenConverter)
                .tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager)
                .reuseRefreshTokens(false)
                .userDetailsService(userDetailsService);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer.realm(realm);
        oauthServer.allowFormAuthenticationForClients();
    }
}
