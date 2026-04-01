package com.chat.api.utils.services.interfaces;

import com.chat.api.modules.user.model.UserModel;

public interface ITokenService {
    String generateRefreshToken(UserModel user);
    String generateToken(UserModel user);
    String validateToken(String token);
}
