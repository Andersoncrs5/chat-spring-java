package com.chat.api.modules.role.services.interfaces;

import com.chat.api.modules.role.model.RoleModel;
import com.chat.api.utils.result.Result;
import jakarta.validation.constraints.NotBlank;

public interface IRoleService {
    Result<RoleModel> create(@NotBlank String name);
}
