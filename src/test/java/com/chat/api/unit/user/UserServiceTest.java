package com.chat.api.unit.user;

import com.chat.api.modules.user.model.UserModel;
import com.chat.api.modules.user.repository.UserRepository;
import com.chat.api.modules.user.services.provider.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private Argon2PasswordEncoder encoder;

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    UserModel user = new UserModel().toBuilder()
            .id(UUID.randomUUID())
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .roles(Set.of("USER"))
            .build();

    @Test
    void shouldCreateUser() {

    }

}
