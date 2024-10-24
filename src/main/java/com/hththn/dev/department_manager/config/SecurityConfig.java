package com.hththn.dev.department_manager.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;




@Configuration
@EnableMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class SecurityConfig{

    //Config encode password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        http
                .csrf(c -> c.disable())


                .cors(c -> c.disable())


                .formLogin(f -> f.disable())


                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )


                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/","/api/v1/auth/login","api/v1/auth/refresh","/h2-console/**").permitAll()  // Allow non-authenticated access to /login
                        .anyRequest().authenticated()  // Others need to be authenticated
                )

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())//Config OAuth2
                        .authenticationEntryPoint(customAuthenticationEntryPoint))//Handle exception in Filter layer

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

}
