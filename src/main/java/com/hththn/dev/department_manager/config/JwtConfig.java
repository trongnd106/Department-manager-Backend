package com.hththn.dev.department_manager.config;

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

    @Value("${authentication.jwt.token-validity-in-seconds}")
    private long jwtExpiration;

    //Người dùng gửi request từ post/login -> hệ thống xác thực và trả về jwt bằng jwtEncoder -> Trong các yêu cầu tiếp theo, client sẽ gửi JWT kèm theo yêu cầu trong header Authorization. -> Spring Security sử dụng JwtDecoder để giải mã và xác thực JWT cho các yêu cầu này.
    //Phương thức này trả về một đối tượng JwtEncoder, chịu trách nhiệm mã hóa và tạo các JWT trong ứng dụng. NimbusJwtEncoder là một triển khai của JwtEncoder, sử dụng thư viện Nimbus JOSE+JWT để mã hóa và ký JWT. Nó yêu cầu một khóa bí mật để mã hóa và ký mã thông báo.
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }
    //Phương thức này tạo ra và trả về một đối tượng SecretKey, là khóa bí mật được sử dụng để mã hóa JWT. Khóa này được tạo từ chuỗi jwtKey (khóa bí mật dưới dạng Base64).
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    //Bean để giải mã các JWT
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
    //khi decode thành công có tác dụng chuyển đổi JWT thành đối tượng Authentication trong Spring Security.
    //Cụ thể, nó giúp xác định các quyền (authorities) mà người dùng có dựa trên thông tin được chứa trong JWT.
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("department");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
    //jwtDecoder để giải mã token và JwtAuthenticationConverter để lấy các quyền từ token đã giải mã đó

}
