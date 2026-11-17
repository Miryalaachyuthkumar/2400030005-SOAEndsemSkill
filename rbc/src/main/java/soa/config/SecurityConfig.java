package soa.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.Claims;
import soa.service.JWTService;

@Configuration
public class SecurityConfig {

    JWTService JWT;

    public SecurityConfig(JWTService JWT) {
        this.JWT = JWT;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // User registration/login
                .requestMatchers("/user/**")
                .permitAll()

                // ADMIN can POST
                .requestMatchers(HttpMethod.POST, "/books/**")
                .hasRole("ADMIN")

                // ADMIN and USER can GET
                .requestMatchers(HttpMethod.GET, "/books/**")
                .hasAnyRole("ADMIN", "USER")

                // ADMIN can PUT
                .requestMatchers(HttpMethod.PUT, "/books/**")
                .hasRole("ADMIN")

                // ADMIN can DELETE
                .requestMatchers(HttpMethod.DELETE, "/books/**")
                .hasRole("ADMIN")

                // Other URLs require authentication
                .anyRequest()
                .authenticated()
            )

            .oauth2ResourceServer(oauth -> oauth
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {

        return token -> {

            Claims claims = JWT.validateToken(token);

            return new Jwt(
                token,
                claims.getIssuedAt().toInstant(),
                claims.getExpiration().toInstant(),
                Map.of("alg", "HS256"),
                claims
            );
        };
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("role");
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setPrincipalClaimName("username");
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }
}