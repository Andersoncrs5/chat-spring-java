package com.chat.api.modules.user.services.interfaces;

import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.utils.annotation.global.isModelInitialized.IsModelInitialized;
import com.chat.api.utils.result.Result;

import java.util.UUID;

public interface IUserService {
    Result<UserModel> create(CreateUserDTO dto);
    void delete(@IsModelInitialized UserModel user);
    Result<UserModel> findById(UUID id);
}
