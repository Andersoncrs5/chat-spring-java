package com.chat.api.modules.role.repository;

import com.chat.api.modules.role.model.RoleModel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface RoleRepository extends MongoRepository<RoleModel, UUID> {
    boolean existsByNameIgnoreCase(@NotBlank String name);
}
