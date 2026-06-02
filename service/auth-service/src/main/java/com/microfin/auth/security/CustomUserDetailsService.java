package com.microfin.auth.security;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microfin.auth.entity.User;
import com.microfin.auth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Loads users by username or email for Spring Security authentication.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String usernameOrEmail) throws UsernameNotFoundException {
        final String identifier = StringUtils.trimToEmpty(usernameOrEmail);
        if (StringUtils.isBlank(identifier)) {
            throw new UsernameNotFoundException("Username/email is required");
        }
        final User user = userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> {
                    LOGGER.debug("User not found: {}", identifier);
                    return new UsernameNotFoundException("User not found: " + identifier);
                });
        return new CustomUserDetails(user);
    }
}
