package com.chat.api.modules.contacts.controller.docs;

import com.chat.api.configs.security.UserPrincipal;
import com.chat.api.modules.contacts.dto.ContactFilterDTO;
import com.chat.api.modules.contacts.dto.CreateContactDTO;
import com.chat.api.modules.contacts.dto.UpdateContactDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

public interface IContactControllerDocs {
    @PostMapping
    ResponseEntity<?> create(
            @RequestBody @Valid CreateContactDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @GetMapping("/{id}")
    ResponseEntity<?> findById(
            @PathVariable @NotNull UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(
            @PathVariable @NotNull UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );

    @GetMapping
    ResponseEntity<?> findAll(
            @Valid ContactFilterDTO filter,
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    );

    @PatchMapping("/{id}")
    ResponseEntity<?> update(
            @PathVariable @NotNull UUID id,
            @RequestBody @Valid UpdateContactDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    );
}
