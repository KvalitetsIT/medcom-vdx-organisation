package dk.medcom.vdx.organisation.configuration;

import dk.medcom.vdx.organisation.converter.CustomJwtGrantedAuthoritiesConverter;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Autowired
    private ServletContext context;

    @Value("${userservice_token_attribute_userrole}")
    private String userRoleAtt;

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .securityMatcher(request -> request.getRequestURI().startsWith(context.getContextPath() + "/services/v2/"))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .anyRequest().authenticated() // Validates jwt token before input validation and authorization validation
                )
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter())))
                .build();
    }

    @Bean
    public JwtAuthenticationConverter customJwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter(userRoleAtt));
        return converter;
    }
}
