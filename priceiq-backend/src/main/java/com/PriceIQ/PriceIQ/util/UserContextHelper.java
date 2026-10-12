package com.PriceIQ.PriceIQ.util;

import com.PriceIQ.PriceIQ.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Utility to safely extract the currently authenticated user's ID
 * from the Spring Security context without duplicating lookup code.
 */
@Component
@RequiredArgsConstructor
public class UserContextHelper {

    private final UserRepository userRepository;

    /**
     * Returns the ID of the currently authenticated user, or empty if
     * there is no authenticated principal (e.g. public endpoints).
     */
    public Optional<Long> getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        String email = auth.getName();
        return userRepository.findByEmail(email).map(u -> u.getId());
    }
}
