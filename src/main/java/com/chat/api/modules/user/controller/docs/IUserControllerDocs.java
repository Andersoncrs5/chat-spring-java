package com.chat.api.modules.user.controller.docs;

import com.chat.api.configs.security.UserPrincipal;
import com.chat.api.modules.user.dto.UpdateUserDTO;
import com.chat.api.modules.user.dto.UserFilterDTO;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface IUserControllerDocs {
    @GetMapping("/me")
    ResponseEntity<?> getById(
            @AuthenticationPrincipal UserPrincipal principal
    );
    @DeleteMapping
    ResponseEntity<?> delete(
            @AuthenticationPrincipal UserPrincipal principal
    );

    @PatchMapping
    ResponseEntity<?> update(
            @RequestBody UpdateUserDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    );

    @GetMapping
    ResponseEntity<?> findAll(
            @ParameterObject @Valid UserFilterDTO filter,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );
}
