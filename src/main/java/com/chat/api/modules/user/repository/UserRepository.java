package com.chat.api.modules.user.repository;

import com.chat.api.modules.user.custom.repository.CustomUserRepository;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.utils.annotation.global.emailConstraint.EmailConstraint;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends MongoRepository<UserModel, UUID>, CustomUserRepository {
    Optional<UserModel> findByEmailIgnoreCase(@EmailConstraint String email);
    boolean existsByEmailIgnoreCase(@EmailConstraint String email);
    boolean existsByUsernameIgnoreCase(String username);

    Optional<UserModel> findByRefreshToken(@NotBlank String refresh);
}
