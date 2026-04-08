package com.chat.api.helps.classes;

import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.utils.res.ResponseToken;

public record UserResponse(
        ResponseToken tokens,
        CreateUserDTO dto
) {
}
