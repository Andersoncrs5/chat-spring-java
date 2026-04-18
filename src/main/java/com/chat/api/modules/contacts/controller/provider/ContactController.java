package com.chat.api.modules.contacts.controller.provider;

import com.chat.api.configs.api.idempotent.Idempotent;
import com.chat.api.configs.security.UserPrincipal;
import com.chat.api.modules.contacts.controller.docs.IContactControllerDocs;
import com.chat.api.modules.contacts.dto.ContactDTO;
import com.chat.api.modules.contacts.dto.ContactFilterDTO;
import com.chat.api.modules.contacts.dto.CreateContactDTO;
import com.chat.api.modules.contacts.dto.UpdateContactDTO;
import com.chat.api.modules.contacts.model.ContactModel;
import com.chat.api.modules.contacts.services.interfaces.IContactService;
import com.chat.api.utils.mapper.contact.ContactMapper;
import com.chat.api.utils.res.ResponseHttp;
import com.chat.api.utils.result.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j @Validated
@RequiredArgsConstructor
@RequestMapping("/v1/contact")
public class ContactController implements IContactControllerDocs {

    private final IContactService service;
    private final ContactMapper mapper;

    @Override
    @Idempotent
    public ResponseEntity<?> findById(
            @PathVariable @NotNull UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    ) {
        Result<ContactModel> result = this.service.findById(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatus())
                    .body(ResponseHttp.error("Contact not found", result.getErrors(), idempotencyKey));
        }

        var dto = mapper.toDto(result.getValue());

        return ResponseEntity
                .status(result.getStatus())
                .body(ResponseHttp.success(dto , "Contact found", idempotencyKey));
    }

    @Override
    @Idempotent
    public ResponseEntity<?> update(
            @PathVariable @NotNull UUID id,
            @RequestBody @Valid UpdateContactDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    ) {
        Result<ContactModel> result = this.service.update(id, dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatus())
                    .body(ResponseHttp.error("Error the update contact", result.getErrors(), idempotencyKey));
        }

        var dtoUpdate = mapper.toDto(result.getValue());

        return ResponseEntity
                .status(result.getStatus())
                .body(ResponseHttp.success(dtoUpdate , "Contact found", idempotencyKey));
    }

    @Idempotent
    @Override
    public ResponseEntity<?> create(
            @RequestBody @Valid CreateContactDTO dto,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    ) {
        Result<ContactModel> result = this.service.create(principal.getId(), dto.contactId(), dto.nickname());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatus())
                    .body(ResponseHttp.error("Error the create contact", result.getErrors(), idempotencyKey));
        }

        var contactDto = mapper.toDto(result.getValue());

        return ResponseEntity
                .status(result.getStatus())
                .body(ResponseHttp.success(contactDto , "Contact created", idempotencyKey));
    }

    @Idempotent
    @Override
    public ResponseEntity<?> delete(
            @PathVariable @NotNull UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestHeader("X-Idempotency-Key") @NotBlank String idempotencyKey
    ) {
        Result<Void> result = this.service.deleteById(id);

        if (result.isFailure()) {
            return ResponseEntity
                .status(result.getStatus())
                .body(ResponseHttp.error("Contact not found", result.getErrors(), idempotencyKey));
        }

        return ResponseEntity
            .status(result.getStatus())
            .body(ResponseHttp.success(null , "Contact found", idempotencyKey));
    }

    @Override
    public ResponseEntity<?> findAll(
            @Valid @ModelAttribute ContactFilterDTO filter,
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ContactModel> page = this.service.findAll(principal.getId(), filter, pageable);

        Page<ContactDTO> dtoPage = page.map(mapper::toDto);

        return ResponseEntity.ok(
                dtoPage
        );
    }

}
