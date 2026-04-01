package com.chat.api.modules.role.services.provider;

import com.chat.api.modules.role.model.RoleModel;
import com.chat.api.modules.role.repository.RoleRepository;
import com.chat.api.modules.role.services.interfaces.IRoleService;
import com.chat.api.utils.exceptions.InternalServerErrorException;
import com.chat.api.utils.result.Result;
import com.mongodb.DuplicateKeyException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository repository;

    public Result<RoleModel> create(@NotBlank String name) {
        RoleModel role = new RoleModel().toBuilder()
                .id(UUID.randomUUID())
                .name(name.toUpperCase())
                .build();

        try {
            return Result.created(repository.save(role));
        } catch (DuplicateKeyException e) {
            return Result.failure("Role name: " + name + " already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            throw new InternalServerErrorException(e);
        }
    }



}
