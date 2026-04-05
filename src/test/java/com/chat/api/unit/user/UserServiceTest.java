package com.chat.api.unit.user;

import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.dto.UpdateUserDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.modules.user.repository.UserRepository;
import com.chat.api.modules.user.services.provider.UserService;
import com.chat.api.utils.mapper.user.UserMapper;
import com.chat.api.utils.result.Result;
import org.springframework.dao.DuplicateKeyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserMapper mapper;
    @Mock private Argon2PasswordEncoder encoder;
    @Mock private UserRepository repository;

    @InjectMocks
    private UserService service;

    UserModel user = new UserModel().toBuilder()
            .id(UUID.randomUUID())
            .name("user")
            .email("user@gmail.com")
            .password("12345678")
            .roles(Set.of("USER"))
            .build();

    CreateUserDTO dto = new CreateUserDTO(
            user.getName(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getBannerUrl(),
            user.getPhoneNumber()
    );

    UpdateUserDTO updateDTO = new UpdateUserDTO(
            user.getName(),
            user.getUsername(),
            user.getPassword(),
            user.getBannerUrl(),
            user.getPhoneNumber()
    );

    @Test
    void shouldCreateUser() {
        when(mapper.toModel(dto)).thenReturn(user);
        when(encoder.encode(dto.password())).thenReturn(user.getPassword());
        when(repository.save(any())).thenReturn(user);

        Result<UserModel> result = this.service.create(dto);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(201);
        assertThat(result.getValue().getId()).isEqualTo(user.getId());

        verify(repository, times(1)).save(any());
        verify(mapper, times(1)).toModel(dto);
        verify(encoder, times(1)).encode(dto.password());
        verifyNoMoreInteractions(repository, encoder, mapper);

        InOrder order = inOrder(repository, mapper, encoder);

        order.verify(mapper).toModel(dto);
        order.verify(encoder).encode(dto.password());
        order.verify(repository).save(any());
    }

    @Test
    @DisplayName("Should return conflict when email already exists")
    void shouldReturnConflictWhenEmailExists() {
        // Arrange
        DuplicateKeyException ex = mock(DuplicateKeyException.class);
        when(ex.getMessage()).thenReturn("duplicate key error: email");

        when(mapper.toModel(dto)).thenReturn(user);
        when(encoder.encode(dto.password())).thenReturn("hashed_pwd");
        when(repository.save(any())).thenThrow(ex);

        // Act
        Result<UserModel> result = this.service.create(dto);

        // Assert
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getErrors().getFirst())
                .isEqualTo("This email address is already in use.");

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should return conflict when username already exists")
    void shouldReturnConflictWhenUsernameExists() {
        DuplicateKeyException ex = mock(DuplicateKeyException.class);
        when(ex.getMessage()).thenReturn("duplicate key error: username");

        when(mapper.toModel(dto)).thenReturn(user);
        when(encoder.encode(dto.password())).thenReturn("hashed_pwd");
        when(repository.save(any())).thenThrow(ex);

        Result<UserModel> result = this.service.create(dto);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getErrors().getFirst())
                .isEqualTo("This username is already in use.");

        verify(repository).save(any());
    }

    @Test
    @DisplayName("Should return generic duplicate error for other unique indexes")
    void shouldReturnGenericConflictForUnknownIndex() {
        DuplicateKeyException ex = mock(DuplicateKeyException.class);
        when(ex.getMessage()).thenReturn("some random constraint");

        when(mapper.toModel(dto)).thenReturn(user);
        when(encoder.encode(dto.password())).thenReturn("hashed_pwd");
        when(repository.save(any())).thenThrow(ex);

        Result<UserModel> result = this.service.create(dto);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatusCode()).isEqualTo(409);
        assertThat(result.getErrors().getFirst())
                .isEqualTo("Duplicate data detected.");

        verify(repository).save(any());
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(repository).delete(user);

        this.service.delete(user);

        verify(repository, times(1)).delete(user);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should return success when user is found by id")
    void shouldReturnSuccessWhenUserIsFound() {
        UUID id = user.getId();
        when(repository.findById(id)).thenReturn(Optional.of(user));

        Result<UserModel> result = this.service.findById(id);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue()).isEqualTo(user);

        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return not found when user does not exist")
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Result<UserModel> result = this.service.findById(id);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(404);
        assertThat(result.getErrors()).contains("User not found");
        assertThat(result.getValue()).isNull();

        verify(repository, times(1)).findById(id);
    }

    @Test
    void shouldUpdate() {
        doNothing().when(mapper).updateModelFromDto(updateDTO, user);
        when(encoder.encode(anyString())).thenReturn(user.getPassword());
        when(repository.save(any())).thenReturn(user);

        Result<UserModel> result = this.service.update(user, updateDTO);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getStatusCode()).isEqualTo(200);
        assertThat(result.getValue().getId()).isEqualTo(user.getId());

        verify(mapper, times(1)).updateModelFromDto(updateDTO, user);
        verify(encoder, times(1)).encode(user.getPassword());
        verify(repository, times(1)).save(user);
        verifyNoMoreInteractions(mapper, encoder, repository);

        InOrder order = inOrder(mapper, encoder, repository);
        order.verify(mapper).updateModelFromDto(updateDTO, user);
        order.verify(encoder).encode(anyString());
        order.verify(repository).save(any());
    }

    @Test
    void shouldThrowDuplicateKeyExceptionBecauseUsernameAlreadyExists() {
        DuplicateKeyException ex = mock(DuplicateKeyException.class);
        when(ex.getMessage()).thenReturn("duplicate key error: username");

        doNothing().when(mapper).updateModelFromDto(updateDTO, user);
        when(encoder.encode(anyString())).thenReturn(user.getPassword());
        when(repository.save(any())).thenThrow(ex);

        Result<UserModel> result = this.service.update(user, updateDTO);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getValue()).isNull();

        verify(mapper, times(1)).updateModelFromDto(updateDTO, user);
        verify(encoder, times(1)).encode(user.getPassword());
        verify(repository, times(1)).save(user);
        verifyNoMoreInteractions(mapper, encoder, repository);

        InOrder order = inOrder(mapper, encoder, repository);
        order.verify(mapper).updateModelFromDto(updateDTO, user);
        order.verify(encoder).encode(anyString());
        order.verify(repository).save(any());
    }

}
