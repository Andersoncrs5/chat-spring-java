package com.chat.api.modules.user.services.interfaces;

import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.dto.UpdateUserDTO;
import com.chat.api.modules.user.dto.UserFilterDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.utils.annotation.global.emailConstraint.EmailConstraint;
import com.chat.api.utils.annotation.global.isModelInitialized.IsModelInitialized;
import com.chat.api.utils.result.Result;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IUserService {
    Result<Boolean> existsById(UUID id);
    Result<UserModel> create(CreateUserDTO dto);
    void delete(@IsModelInitialized UserModel user);
    Result<UserModel> findById(UUID id);
    Page<UserModel> findAll(
            UserFilterDTO filter,
            Pageable pageable
    );
    Result<UserModel> update(
            @IsModelInitialized UserModel user,
            UpdateUserDTO dto
    );
    Result<UserModel> findByEmail(@EmailConstraint String email);
    Result<UserModel> blockUser(
            UUID userID
    );
    Result<UserModel> setLastLogin(
            UUID userID,
            @NotBlank String refreshToken
    );
    Result<UserModel> setLastLogin(
            UUID userID
    );
    Result<UserModel> findByRefreshToken(@NotBlank String refresh);
}
