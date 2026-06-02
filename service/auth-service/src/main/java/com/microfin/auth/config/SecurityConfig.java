package com.microfin.auth.config;

import java.util.Collection;
import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.microfin.auth.constants.AuthConstants;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

/**
 * Default application security: Basic Auth + Bearer JWT (Resource Server).
 *
 * <p>Note: the OAuth2 Authorization Server filter chain is registered separately in
 * {@link AuthorizationServerConfig} with a higher precedence so that {@code /oauth2/**}
 * endpoints are handled before this generic chain.</p>
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/health",
            "/actuator/**",
            AuthConstants.AUTH_BASE + "/register",
            AuthConstants.AUTH_BASE + "/login",
            AuthConstants.AUTH_BASE + "/refresh",
            "/.well-known/**",
            "/oauth2/jwks"
    };

    @Bean
    public SecurityFilterChain appSecurityFilterChain(final HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                    .requestMatchers(AuthConstants.ADMIN_BASE + "/**").hasRole("ADMIN")
                    .anyRequest().authenticated())
            .httpBasic(httpBasic -> {})
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public JwtEncoder jwtEncoder(final JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(final JWKSource<SecurityContext> jwkSource) throws Exception {
        final JWKSet jwkSet = new JWKSet(jwkSource.get(new JWKSelector(
                new com.nimbusds.jose.jwk.JWKMatcher.Builder().build()), null));
        final RSAKey rsaKey = (RSAKey) jwkSet.getKeys().get(0);
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    /**
     * Maps the {@code roles} claim of our JWTs to Spring Security authorities.
     * Roles are stored already prefixed with {@code ROLE_}, so no extra prefix is added.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        final Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter = jwt -> {
            final Object claim = jwt.getClaim(AuthConstants.CLAIM_ROLES);
            if (!(claim instanceof Collection<?> raw)) {
                return List.of();
            }
            return raw.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast)
                    .toList();
        };
        final JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        converter.setPrincipalClaimName("sub");
        return converter;
    }
}
