package com.chat.api.unit.role;

import com.chat.api.modules.role.model.RoleModel;
import com.chat.api.modules.role.repository.RoleRepository;
import com.chat.api.modules.role.services.provider.RoleService;
import com.chat.api.utils.result.Result;
import com.mongodb.DuplicateKeyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    @Mock private RoleRepository repository;

    @InjectMocks private RoleService service;

    RoleModel role = new RoleModel().toBuilder()
            .id(UUID.randomUUID())
            .name("USER")
            .version(1L)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    @Test
    void shouldCreateRole() {
        when(repository.save(any()))
                .thenReturn(role);

        Result<RoleModel> roleUser = this.service.create("USER");

        assertThat(roleUser.isSuccess()).isTrue();
        assertThat(roleUser.getValue().getId()).isEqualTo(role.getId());

        verify(repository, times(1)).save(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFailureWhenRoleNameAlreadyExists() {
        DuplicateKeyException mockException = mock(DuplicateKeyException.class);

        when(repository.save(any())).thenThrow(mockException);

        Result<RoleModel> result = this.service.create("USER");

        assertThat(result.isFailure()).isTrue();

        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getErrors().getFirst()).contains("already exists");

        verify(repository, times(1)).save(any());
    }

}
