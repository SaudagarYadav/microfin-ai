package com.microfin.auth.service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microfin.auth.constants.AuthConstants;
import com.microfin.auth.dto.AuthResponse;
import com.microfin.auth.dto.LoginRequest;
import com.microfin.auth.dto.RegisterRequest;
import com.microfin.auth.dto.UserResponse;
import com.microfin.auth.entity.RefreshToken;
import com.microfin.auth.entity.Role;
import com.microfin.auth.entity.User;
import com.microfin.auth.exception.AuthException;
import com.microfin.auth.exception.DuplicateUserException;
import com.microfin.auth.repository.RoleRepository;
import com.microfin.auth.repository.UserRepository;
import com.microfin.auth.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

/**
 * Orchestrates registration, password authentication and token issuance.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public UserResponse register(final RegisterRequest request) {
        final String username = StringUtils.trimToEmpty(request.username());
        final String email = StringUtils.trimToEmpty(request.email()).toLowerCase();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException("Username already in use");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("Email already in use");
        }

        final Role userRole = roleRepository.findByRoleName(AuthConstants.ROLE_USER)
                .orElseThrow(() -> new AuthException("Default role not configured"));

        final Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        final User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .firstName(StringUtils.trimToNull(request.firstName()))
                .lastName(StringUtils.trimToNull(request.lastName()))
                .provider(AuthConstants.PROVIDER_LOCAL)
                .enabled(Boolean.TRUE)
                .accountNonLocked(Boolean.TRUE)
                .accountNonExpired(Boolean.TRUE)
                .credentialsNonExpired(Boolean.TRUE)
                .roles(roles)
                .build();

        final User saved = userRepository.save(user);
        LOGGER.info("Registered new user id={}, username={}", saved.getId(), saved.getUsername());
        return toUserResponse(saved);
    }

    @Transactional
    public AuthResponse login(final LoginRequest request) {
        final String username = StringUtils.trimToEmpty(request.username());

        final Authentication authToken = new UsernamePasswordAuthenticationToken(username, request.password());
        final Authentication authenticated = authenticationManager.authenticate(authToken);

        final CustomUserDetails principal = (CustomUserDetails) authenticated.getPrincipal();
        final User user = principal.getDomainUser();

        return issueTokens(user);
    }

    /**
     * Issues access + refresh tokens for an already-authenticated user
     * (e.g. resolved from HTTP Basic by Spring Security). The caller is
     * responsible for ensuring the {@link Authentication} is valid.
     */
    @Transactional
    public AuthResponse issueTokensForUsername(final String rawUsername) {
        final String username = StringUtils.trimToEmpty(rawUsername);
        if (username.isEmpty()) {
            throw new AuthException("Authenticated principal has no username");
        }
        final User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException("Authenticated user not found: " + username));
        LOGGER.info("Issuing tokens via principal exchange for userId={}", user.getId());
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(final String refreshTokenValue) {
        final RefreshToken existing = refreshTokenService.validate(refreshTokenValue);
        final RefreshToken rotated = refreshTokenService.rotate(existing);
        final User user = rotated.getUser();

        final JwtService.IssuedToken access = jwtService.issueAccessToken(user);
        LOGGER.info("Refreshed tokens for userId={}", user.getId());
        // User details are intentionally omitted on refresh; the client
        // already has them from the original login response.
        return new AuthResponse(
                AuthConstants.BEARER_PREFIX.trim(),
                access.token(),
                rotated.getToken(),
                jwtService.getAccessTokenTtlSeconds(),
                null);
    }

    @Transactional
    public void logout(final String refreshTokenValue) {
        refreshTokenService.revoke(refreshTokenValue);
    }

    private AuthResponse issueTokens(final User user) {
        final JwtService.IssuedToken access = jwtService.issueAccessToken(user);
        final RefreshToken refresh = refreshTokenService.create(user);
        return new AuthResponse(
                AuthConstants.BEARER_PREFIX.trim(),
                access.token(),
                refresh.getToken(),
                jwtService.getAccessTokenTtlSeconds(),
                toUserResponse(user));
    }

    public UserResponse toUserResponse(final User user) {
        final Set<String> roleNames = user.getRoles().stream()
                .map(Role::getRoleName)
                .collect(Collectors.toUnmodifiableSet());
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roleNames);
    }
}
