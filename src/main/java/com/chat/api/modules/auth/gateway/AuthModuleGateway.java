package com.chat.api.modules.auth.gateway;

import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.modules.user.services.interfaces.IUserService;
import com.chat.api.utils.result.Result;
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

    public Result<UserModel> setLastLogin(UUID id) {
        return this.userService.setLastLogin(id);
    }

    public Result<UserModel> createUser(CreateUserDTO dto) {
        return this.userService.create(dto);
    }

}
