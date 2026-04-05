package com.chat.api.modules.auth.controller.provider;

import com.chat.api.modules.auth.controller.docs.IAuthControllerDocs;
import com.chat.api.modules.user.services.interfaces.IUserService;
import com.chat.api.utils.services.interfaces.ITokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth/")
public class AuthController implements IAuthControllerDocs {

    private final IUserService service;
    private final ITokenService tokenService;



}
