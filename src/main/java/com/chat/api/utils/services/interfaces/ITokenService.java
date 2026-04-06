package com.chat.api.utils.services.interfaces;

import com.chat.api.modules.user.model.UserModel;
import com.chat.api.utils.annotation.global.isModelInitialized.IsModelInitialized;
import com.chat.api.utils.res.ResponseToken;
import jakarta.validation.constraints.NotBlank;

public interface ITokenService {
    String generateRefreshToken(UserModel user);
    String generateToken(UserModel user);
    String validateToken(@NotBlank String token);
    ResponseToken generateResponseToken(@IsModelInitialized UserModel user);
}
