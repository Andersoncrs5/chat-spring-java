package com.chat.api.services.providers;

import com.chat.api.configs.parameters.JwtParameter;
import com.chat.api.services.interfaces.ITokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {

    private final JwtParameter jwtParameter;

}
