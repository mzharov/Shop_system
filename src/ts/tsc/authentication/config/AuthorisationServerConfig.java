package ts.tsc.authentication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorisationServerConfig
        extends AuthorizationServerConfigurerAdapter {

    @Value("${security.client-id}")
    private String clientID;
    @Value("${security.token.secret-key}")
    private String secret;
    @Value("${security.token.grant-types}")
    private String[] grantTypes;
    @Value("${security.token.authorities}")
    private String[] authorities;
    @Value("${security.token.scopes}")
    private String[] scopes;
    @Value("${security.token.access.expire-length}")
    private int accessTokenExpireLength;
    @Value("${security.token.refresh.expire-length}")
    private int refreshTokenExpireLength;
    @Value("${security.token.realm}")
    private String realm;

    private final TokenStore tokenStore;
    private final JwtAccessTokenConverter accessTokenConverter;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    DataSource dataSource;

    @Autowired
    public AuthorisationServerConfig(TokenStore tokenStore,
                                     JwtAccessTokenConverter accessTokenConverter,
                                     @Qualifier("authenticationManagerBean")
                                                 AuthenticationManager authenticationManager,
                                     @Qualifier("userDetailService")
                                                 UserDetailsService userDetailsService,
                                     PasswordEncoder passwordEncoder) {
        this.tokenStore = tokenStore;
        this.accessTokenConverter = accessTokenConverter;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient(clientID)
                .secret(passwordEncoder.encode(secret))
                .authorizedGrantTypes(grantTypes)
                .authorities(authorities)
                .scopes(scopes)
                .accessTokenValiditySeconds(accessTokenExpireLength)
                .refreshTokenValiditySeconds(refreshTokenExpireLength);
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.tokenStore(tokenStore)
                .accessTokenConverter(accessTokenConverter)
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
