package com.chat.api.modules.user.services.provider;

import com.chat.api.modules.user.repository.UserRepository;
import com.chat.api.modules.user.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository repository;
    private final Argon2PasswordEncoder encoder;



}
