package com.chat.api.modules.user.services.provider;

import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.dto.UpdateUserDTO;
import com.chat.api.modules.user.dto.UserFilterDTO;
import com.chat.api.modules.user.gateway.UserModuleGateway;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.modules.user.repository.UserRepository;
import com.chat.api.modules.user.services.interfaces.IUserService;
import com.chat.api.utils.annotation.global.emailConstraint.EmailConstraint;
import com.chat.api.utils.annotation.global.isModelInitialized.IsModelInitialized;
import com.chat.api.utils.mapper.user.UserMapper;
import com.chat.api.utils.result.Result;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final Argon2PasswordEncoder encoder;
    private final UserModuleGateway gateway;

    @Override
    public Result<UserModel> create(CreateUserDTO dto) {
        UserModel model = this.mapper.toModel(dto);
        model.setPassword(encoder.encode(dto.password()));

        try {
            UserModel save = this.repository.save(model);

            return Result.created(save);
        } catch (DuplicateKeyException e) {

            var message = e.getMessage();
            if (message != null && message.contains("email")) {
                return Result.conflict("This email address is already in use.");
            }

            if (message != null && message.contains("username")) {
                return Result.conflict("This username is already in use.");
            }
            return Result.conflict("Duplicate data detected.");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void delete(@IsModelInitialized UserModel user) {
        this.repository.delete(user);
    }

    @Override
    public Result<UserModel> findById(UUID id) {
        Optional<UserModel> optional = this.repository.findById(id);

        return optional
                .map(Result::success)
                .orElseGet(() -> Result.notFound("User not found"));
    }

    @Override
    public Page<UserModel> findAll(UserFilterDTO filter, Pageable pageable) {
        return this.repository.findByFilter(filter, pageable);
    }

    @Override
    public Result<UserModel> update(
            @IsModelInitialized UserModel user,
            UpdateUserDTO dto
    ) {
        this.mapper.updateModelFromDto(dto, user);

        if (dto.password() != null)
            user.setPassword(encoder.encode(dto.password()));

        try {
            UserModel save = this.repository.save(user);

            return Result.success(save);
        } catch (DuplicateKeyException e) {
            String message = e.getMessage();

            if (message != null && message.contains("username")) {
                return Result.conflict("This username is already in use.");
            }

            return Result.conflict("Duplicate data detected.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<UserModel> findByEmail(@EmailConstraint String email) {
        Optional<UserModel> optional = this.repository.findByEmailIgnoreCase(email);

        return optional
                .map(Result::success)
                .orElseGet(() -> Result.notFound("User not found"));
    }

    @Override
    public Result<UserModel> blockUser(
            UUID userID
    ) {
        Optional<UserModel> optional = this.repository.findById(userID);

        if (optional.isEmpty()) return Result.notFound("User not found");

        UserModel user = optional.get();

        try {
            user.recordFailedLogin();
            UserModel save = this.repository.save(user);

            return Result.success(save);
        } catch (DuplicateKeyException e) {
            String message = e.getMessage();

            if (message != null && message.contains("username")) {
                return Result.conflict("This username is already in use.");
            }

            return Result.conflict("Duplicate data detected.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<UserModel> setLastLogin(
            UUID userID,
            String refreshToken
    ) {
        Optional<UserModel> optional = this.repository.findById(userID);

        if (optional.isEmpty()) return Result.notFound("User not found");

        UserModel user = optional.get();

        try {
            user.resetLoginAttempts();
            user.setLastActiveAt(Instant.now());
            user.setRefreshToken(refreshToken);

            UserModel save = this.repository.save(user);

            return Result.success(save);
        } catch (DuplicateKeyException e) {
            String message = e.getMessage();

            if (message != null && message.contains("username")) {
                return Result.conflict("This username is already in use.");
            }

            return Result.conflict("Duplicate data detected.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<UserModel> setLastLogin(
            UUID userID
    ) {
        Optional<UserModel> optional = this.repository.findById(userID);

        if (optional.isEmpty()) return Result.notFound("User not found");

        UserModel user = optional.get();

        try {
            user.resetLoginAttempts();
            user.setLastActiveAt(Instant.now());

            UserModel save = this.repository.save(user);
            log.info("user saved : {}", save);
            return Result.success(save);
        } catch (DuplicateKeyException e) {
            String message = e.getMessage();

            if (message != null && message.contains("username")) {
                return Result.conflict("This username is already in use.");
            }

            return Result.conflict("Duplicate data detected.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<UserModel> findByRefreshToken(@NotBlank String refresh) {
        Optional<UserModel> opt = repository.findByRefreshTokenIgnoreCase(refresh);

        return opt.map(Result::success).orElseGet(() -> Result.notFound("User not found"));
    }

}
