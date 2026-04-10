package dk.medcom.vdx.organisation.integrationtest.v2.helper;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class JwtTokenGenerator {
    private final JwtEncoder encoder;

    public JwtTokenGenerator(RSAPublicKey publicKey, RSAPrivateKey privateKey, String keyId) {
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(keyId)
                .build();

        this.encoder = new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(rsaKey)));
    }

    public String createToken(long expiresInSeconds, String iss, String organisation, List<String> roles, String email) {
        var claims = JwtClaimsSet.builder()
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(expiresInSeconds))
                .issuer(iss)
                .subject(UUID.randomUUID().toString())
                .claim("dk:medcom:organisation_id", organisation)
                .claim("dk:medcom:video:role", roles)
                .claim("dk:medcom:email", email);

        return encoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build(),
                claims.build()
        )).getTokenValue();
    }
}