package dk.medcom.vdx.organisation.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private final static Logger logger = LoggerFactory.getLogger(CustomJwtGrantedAuthoritiesConverter.class);
    private final String userRoleAtt;

    public CustomJwtGrantedAuthoritiesConverter(String userRoleAtt) {
        this.userRoleAtt = userRoleAtt;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        logger.debug("Convert user role attribute claims in jwt token to granted authorities.");
        var userRoleClaim = jwt.getClaimAsStringList(userRoleAtt);

        if (userRoleClaim == null) {
            logger.warn("Found no claims of type {} in jwt token.", userRoleAtt);
            return List.of();
        }

        return userRoleClaim.stream().map(role -> new SimpleGrantedAuthority("ROLE_ATT_" + role))
                .collect(Collectors.toList());
    }
}