package com.chat.api.modules.auth.services.provider;

import com.chat.api.modules.auth.dto.LoginRequestDTO;
import com.chat.api.modules.auth.gateway.AuthModuleGateway;
import com.chat.api.modules.auth.services.interfaces.IAuthService;
import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.utils.res.ResponseToken;
import com.chat.api.utils.result.Result;
import com.chat.api.utils.services.interfaces.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final ITokenService tokenService;
    private final Argon2PasswordEncoder encoder;
    private final AuthModuleGateway gateway;

    @Transactional
    public Result<ResponseToken> login(LoginRequestDTO dto) {
        return gateway.findUserByEmail(dto.email())
                .map(user -> {
                    if (user.isLoginBlocked()) {
                        return Result.<ResponseToken>failure("Account blocked", HttpStatus.FORBIDDEN);
                    }

                    if (!encoder.matches(dto.password(), user.getPassword())) {
                        Result<UserModel> result = gateway.blockUser(user.getId());

                        if (result.isFailure()) {
                            return Result.<ResponseToken>failure(result.getErrors(), result.getStatus());
                        }

                        return Result.<ResponseToken>failure("Invalid credentials", HttpStatus.UNAUTHORIZED);
                    }

                    Result<UserModel> result = gateway.setLastLogin(user.getId());

                    if (result.isFailure()) {
                        return Result.<ResponseToken>failure(result.getErrors(), result.getStatus());
                    }

                    ResponseToken response = tokenService.generateResponseToken(user);
                    return Result.success(response);
                })
                .orElseGet(() -> Result.failure("User not found", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public Result<ResponseToken> create(CreateUserDTO dto) {
        Result<UserModel> userResult = this.gateway.createUser(dto);

        if (userResult.isFailure()) {
            return Result.failure(userResult.getErrors(), userResult.getStatus());
        }

        ResponseToken response = tokenService.generateResponseToken(userResult.getValue());
        return Result.success(response);
    }

}
