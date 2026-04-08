package com.chat.api.modules.auth.controller.docs;

import com.chat.api.modules.auth.dto.LoginRequestDTO;
import com.chat.api.modules.user.dto.CreateUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IAuthControllerDocs {
    @PostMapping("/register")
    ResponseEntity<?> create(
            @Valid @RequestBody CreateUserDTO dto,
            HttpServletRequest request
    );
    @PostMapping("/login")
    ResponseEntity<?> login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletRequest request
    );
}
