package com.chat.api.modules.auth.controller.provider;

import com.chat.api.modules.auth.controller.docs.IAuthControllerDocs;
import com.chat.api.modules.auth.dto.LoginRequestDTO;
import com.chat.api.modules.auth.services.interfaces.IAuthService;
import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.utils.mapper.user.UserMapper;
import com.chat.api.utils.res.ResponseHttp;
import com.chat.api.utils.res.ResponseToken;
import com.chat.api.utils.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth/")
public class AuthController implements IAuthControllerDocs {

    private final IAuthService service;
    private final UserMapper mapper;

    @Override
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletRequest request
    ) {
        Result<ResponseToken> tokenResult = this.service.login(dto);

        if (tokenResult.isFailure()) {
            return ResponseEntity
                    .status(tokenResult.getStatus())
                    .body(ResponseHttp.error("Error the to log user", tokenResult.getErrors()));
        }

        return ResponseEntity
                .ok(ResponseHttp.success(tokenResult.getValue(), "User logged"));
    }

    @Override
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateUserDTO dto,
            HttpServletRequest request
    ) {
        Result<ResponseToken> tokenResult = this.service.create(dto);

        if (tokenResult.isFailure()) {
            return ResponseEntity
                    .status(tokenResult.getStatus())
                    .body(ResponseHttp.error("Error the create user", tokenResult.getErrors()));
        }

        return ResponseEntity
                .status(201)
                .body(ResponseHttp.success(tokenResult.getValue(), "User created"));
    }

    @Override
    public ResponseEntity<?> refreshToken(
            @PathVariable String refreshToken,
            HttpServletRequest request
    ) {
        Result<ResponseToken> result = this.service.refreshToken(refreshToken);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatus())
                    .body(ResponseHttp.error("Error the to log user", result.getErrors()));
        }

        return ResponseEntity
                .ok(ResponseHttp.success(result.getValue(), "Tokens created"));

    }

}
