package com.chat.api.modules.auth.services.interfaces;

import com.chat.api.modules.auth.dto.LoginRequestDTO;
import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.utils.res.ResponseToken;
import com.chat.api.utils.result.Result;
import jakarta.validation.constraints.NotBlank;

public interface IAuthService {
    Result<ResponseToken> login(LoginRequestDTO dto);
    Result<ResponseToken> create(CreateUserDTO dto);
    Result<ResponseToken> refreshToken(@NotBlank String refreshToken);
}
