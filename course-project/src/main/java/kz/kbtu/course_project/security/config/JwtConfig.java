package kz.kbtu.course_project.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class JwtConfig {

    @Bean
    JwtEncoder jwtEncoder(
            @Value("${rsa.private-key}") Resource privateKeyResource,
            @Value("${rsa.public-key}") Resource publicKeyResource) throws IOException {
        RSAPrivateKey privateKey;
        RSAPublicKey publicKey;
        try (var privateInput = privateKeyResource.getInputStream();
             var publicInput = publicKeyResource.getInputStream()) {
            privateKey = RsaKeyConverters.pkcs8().convert(privateInput);
            publicKey = RsaKeyConverters.x509().convert(publicInput);
        }

        var jwk = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("spring-course-project")
                .build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<SecurityContext>(new JWKSet(jwk)));
    }

    @Bean
    JwtDecoder jwtDecoder(
            @Value("${rsa.public-key}") Resource publicKeyResource,
            @Value("${app.security.jwt.issuer:cyberpunk-red-api}") String issuer)
            throws IOException {
        RSAPublicKey publicKey;
        try (var input = publicKeyResource.getInputStream()) {
            publicKey = RsaKeyConverters.x509().convert(input);
        }
        var decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }

}
