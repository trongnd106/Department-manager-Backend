package com.hththn.dev.department_manager.config;

import com.hththn.dev.department_manager.service.SecurityUtil;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class JwtConfig {

    //Environment variable defined in application.yaml
    @Value("${authentication.jwt.base64-secret}")
    private String jwtKey;

    @Value("${authentication.jwt.access-token-validity-in-seconds}")
    private long jwtExpiration;

    //When user sends request from post/login -> The system authenticates and returns a JWT using jwtEncoder. -> In subsequent requests, the client will send the JWT along with the request in the Authorization header. -> Spring Security uses JwtDecoder to decode and authenticate the JWT for these requests.
    //This method returns a JwtEncoder object, Which is responsible for encoding and creating JWTs in the application.
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }
    //This method creates and returns a SecretKey object, which is the secret key used to encode the JWT. This key is generated from the jwtKey string (a secret key in Base64 format).
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    //Bean for decoding JWT tokens
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }
    //When successfully decoded, it converts the JWT into an Authentication object in Spring Security.
    //Specifically, it helps determine the authorities the user has based on the information contained in the JWT.
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("permission"); //This sets the claim name from which the authorities will be extracted. In this case, it's using the "permission" claim from the JWT. This means that the authorities (roles/permissions) for the user will be based on the "department" claim in the JWT.
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
    //jwtDecoder is used to decode token and JwtAuthenticationConverter is used to extract the authorities (roles/permissions) from the decoded token.

}
