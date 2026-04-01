package com.chat.api.configs.security;

import com.chat.api.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationEvents {

    private final UserRepository userRepository;

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String email = event.getAuthentication().getName();

        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            user.recordFailedLogin();
            userRepository.save(user);
            log.warn("Tentativa de login falha para o usuário: {}. Tentativas: {}", email, user.getAttemptsLogin());
        });
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();

        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            if (user.getAttemptsLogin() > 0) {
                user.resetLoginAttempts();
                userRepository.save(user);
            }
        });
    }
}
