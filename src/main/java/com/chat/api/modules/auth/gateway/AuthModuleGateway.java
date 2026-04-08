package com.chat.api.modules.auth.gateway;

import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.modules.user.services.interfaces.IUserService;
import com.chat.api.utils.result.Result;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthModuleGateway {

    private final IUserService userService;

    public Result<UserModel> findUserByEmail(String email) {
        return this.userService.findByEmail(email);
    }

    public Result<UserModel> blockUser(UUID id) {
        return this.userService.blockUser(id);
    }

    public Result<UserModel> setLastLogin(UUID id, String refresh) {
        return this.userService.setLastLogin(id, refresh);
    }

    public Result<UserModel> createUser(CreateUserDTO dto) {
        return this.userService.create(dto);
    }

    public Result<UserModel> findUserByRefreshToken(@NotBlank String refreshToken) {
        return userService.findByRefreshToken(refreshToken);
    }

}
