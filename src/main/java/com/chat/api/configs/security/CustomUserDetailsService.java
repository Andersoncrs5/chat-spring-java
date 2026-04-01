package com.chat.api.configs.security;

import com.chat.api.modules.user.model.UserModel;
import com.chat.api.modules.user.repository.UserRepository;
import com.chat.api.utils.annotation.global.emailConstraint.EmailConstraint;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public @NonNull UserDetails loadUserByUsername(@EmailConstraint @NonNull String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.isLoginBlocked()) {
            throw new LockedException("User account is temporarily blocked until " + user.getLoginBlockAt());
        }

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList(),
                user
        );
    }
}
