package com.chat.api.unit.auth;

import com.chat.api.modules.auth.dto.LoginRequestDTO;
import com.chat.api.modules.auth.gateway.AuthModuleGateway;
import com.chat.api.modules.auth.services.provider.AuthService;
import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.dto.UserDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.utils.res.ResponseToken;
import com.chat.api.utils.result.Result;
import com.chat.api.utils.services.interfaces.ITokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock private AuthModuleGateway gateway;
    @Mock private ITokenService tokenService;
    @Mock private Argon2PasswordEncoder encoder;

    @InjectMocks private AuthService service;

    UserModel user = new UserModel().toBuilder()
            .id(UUID.randomUUID())
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .roles(Set.of("USER"))
            .bio("AnyBio")
            .roles(Set.of("USER"))
            .refreshToken("refresh-token")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    UserDTO userDTO = new UserDTO(
            user.getId(),
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getBannerUrl(),
            user.getBio(),
            user.getPhoneNumber(),
            user.getVersion(),
            user.getLastActiveAt(),
            user.getRoles(),
            Instant.now(),
            Instant.now()
    );

    ResponseToken expectedToken = new ResponseToken(
            "access-token",
            "refresh-token",
            "Bearer",
            Instant.now(),
            Instant.now(),
            userDTO
    );

    LoginRequestDTO dto = new LoginRequestDTO(
            user.getEmail(),
            user.getPassword()
    );

    CreateUserDTO createUserDTO = new CreateUserDTO(
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getBannerUrl(),
            user.getBio(),
            user.getPhoneNumber()
    );

    @Test
    void shouldMakeLoginUserSuccessfully() {
        when(gateway.findUserByEmail(dto.email())).thenReturn(Result.success(user));
        when(encoder.matches(dto.password(), user.getPassword())).thenReturn(true);
        when(tokenService.generateResponseToken(user)).thenReturn(expectedToken);
        when(gateway.setLastLogin(user.getId(), expectedToken.refreshToken())).thenReturn(Result.success(user));

        Result<ResponseToken> result = service.login(dto);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().token()).isEqualTo(expectedToken.token());

        verify(gateway, times(1)).findUserByEmail(dto.email());
        verify(tokenService, times(1)).generateResponseToken(user);
        verify(gateway, times(1)).setLastLogin(user.getId(), expectedToken.refreshToken());

        InOrder order = inOrder(gateway, encoder, tokenService);

        order.verify(gateway).findUserByEmail(dto.email());
        order.verify(encoder).matches(dto.password(), user.getPassword());
        order.verify(tokenService).generateResponseToken(user);
        order.verify(gateway).setLastLogin(user.getId(), expectedToken.refreshToken());
    }

    @Test
    void shouldFailTheMakeLogin() {
        when(gateway.findUserByEmail(dto.email())).thenReturn(Result.notFound("User not found"));

        Result<ResponseToken> login = this.service.login(dto);

        assertThat(login.isFailure()).isTrue();
        assertThat(login.getValue()).isNull();
        assertThat(login.getStatusCode()).isEqualTo(404);

        verify(gateway, times(1)).findUserByEmail(dto.email());
        verify(gateway, never()).setLastLogin(user.getId(), expectedToken.refreshToken());
        verify(gateway, never()).blockUser(any());
        verify(tokenService, never()).generateResponseToken(user);

        verifyNoMoreInteractions(gateway, tokenService, gateway);
    }

    @Test
    void shouldFailTheMakeLoginBecausePasswordWrong() {
        when(gateway.findUserByEmail(dto.email())).thenReturn(Result.success(user));
        when(encoder.matches(dto.password(), user.getPassword())).thenReturn(false);
        when(gateway.blockUser(any(UUID.class))).thenReturn(Result.success(user));

        Result<ResponseToken> login = this.service.login(dto);

        assertThat(login.isFailure()).isTrue();
        assertThat(login.getValue()).isNull();
        assertThat(login.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(login.getErrors().getFirst()).isEqualTo("Invalid credentials");

        verify(gateway, times(1)).findUserByEmail(dto.email());
        verify(encoder, times(1)).matches(dto.password(), user.getPassword());
        verify(gateway, times(1)).blockUser(user.getId());
        verify(tokenService, never()).generateResponseToken(user);
        verify(gateway, never()).setLastLogin(user.getId(), expectedToken.refreshToken());

        verifyNoMoreInteractions(gateway, tokenService, gateway);
    }

    @Test
    void shouldCreateUser() {
        when(gateway.createUser(createUserDTO)).thenReturn(Result.created(user));
        when(tokenService.generateResponseToken(user)).thenReturn(expectedToken);

        Result<ResponseToken> tokenResult = this.service.create(createUserDTO);

        assertThat(tokenResult.isSuccess()).isTrue();
        assertThat(tokenResult.getValue().token()).isEqualTo(expectedToken.token());
        assertThat(tokenResult.getValue().refreshToken()).isEqualTo(expectedToken.refreshToken());

        verify(gateway, atMostOnce()).createUser(any());
        verify(tokenService, atMostOnce()).generateResponseToken(user);

        verifyNoMoreInteractions(gateway, tokenService);
        verifyNoInteractions(encoder);
    }

    @Test
    void shouldFailTheCreateUser() {
        when(gateway.createUser(createUserDTO)).thenReturn(Result.failure("Error", HttpStatus.BAD_REQUEST));

        Result<ResponseToken> tokenResult = this.service.create(createUserDTO);

        assertThat(tokenResult.isFailure()).isTrue();
        assertThat(tokenResult.getValue()).isNull();

        verify(gateway, atMostOnce()).createUser(any());
        verifyNoMoreInteractions(gateway);
        verifyNoInteractions(tokenService, encoder);
    }

    @Test
    void shouldMakeRefreshTokens() {
        when(gateway.findUserByRefreshToken(user.getRefreshToken()))
                .thenReturn(Result.success(user));
        when(tokenService.generateResponseToken(user))
                .thenReturn(expectedToken);
        when(gateway.setLastLogin(user.getId(), expectedToken.refreshToken()))
                .thenReturn(Result.success(user));

        Result<ResponseToken> tokenResult = this.service.refreshToken(user.getRefreshToken());

        assertThat(tokenResult.isSuccess()).isTrue();
        assertThat(tokenResult.getValue().token()).isEqualTo(expectedToken.token());
        assertThat(tokenResult.getValue().refreshToken()).isEqualTo(expectedToken.refreshToken());

        verify(gateway, times(1)).findUserByRefreshToken(user.getRefreshToken());
        verify(tokenService, times(1)).generateResponseToken(user);
        verify(gateway, times(1)).setLastLogin(user.getId(), expectedToken.refreshToken());

        verifyNoMoreInteractions(gateway, tokenService);

        InOrder order = inOrder(gateway, tokenService);

        order.verify(gateway).findUserByRefreshToken(user.getRefreshToken());
        order.verify(tokenService).generateResponseToken(user);
        order.verify(gateway).setLastLogin(user.getId(), expectedToken.refreshToken());
    }

    @Test
    void shouldFailBecauseUserNotFoundTheFindByRefreshTokenInMethodRefreshToken() {
        when(gateway.findUserByRefreshToken(user.getRefreshToken()))
                .thenReturn(Result.notFound("User not found"));

        Result<ResponseToken> result = this.service.refreshToken(user.getRefreshToken());

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrors().getFirst()).isEqualTo("User not found");
        assertThat(result.getValue()).isNull();

        verify(gateway, atMostOnce()).findUserByRefreshToken(user.getRefreshToken());
        verify(tokenService, never()).generateResponseToken(user);
        verify(gateway, never()).setLastLogin(user.getId(), expectedToken.refreshToken());

        verifyNoMoreInteractions(gateway, tokenService);
    }

    @Test
    void shouldFailTheMakeRefreshTokensBecauseSetLastLoginFail() {
        when(gateway.findUserByRefreshToken(user.getRefreshToken()))
                .thenReturn(Result.success(user));
        when(tokenService.generateResponseToken(user))
                .thenReturn(expectedToken);
        when(gateway.setLastLogin(user.getId(), expectedToken.refreshToken()))
                .thenReturn(Result.notFound("User not found"));

        Result<ResponseToken> tokenResult = this.service.refreshToken(user.getRefreshToken());

        assertThat(tokenResult.isFailure()).isTrue();
        assertThat(tokenResult.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(tokenResult.getValue()).isNull();

        verify(gateway, atMostOnce()).findUserByRefreshToken(user.getRefreshToken());
        verify(tokenService, atMostOnce()).generateResponseToken(user);
        verify(gateway, atMostOnce()).setLastLogin(user.getId(), expectedToken.refreshToken());

        verifyNoMoreInteractions(gateway, tokenService);

        InOrder order = inOrder(gateway, tokenService);

        order.verify(gateway).findUserByRefreshToken(user.getRefreshToken());
        order.verify(tokenService).generateResponseToken(user);
        order.verify(gateway).setLastLogin(user.getId(), expectedToken.refreshToken());
    }


}
