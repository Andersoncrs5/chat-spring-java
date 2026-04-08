package com.chat.api.modules.user.controller.provider;

import com.chat.api.configs.security.UserPrincipal;
import com.chat.api.modules.user.controller.docs.IUserControllerDocs;
import com.chat.api.modules.user.dto.UpdateUserDTO;
import com.chat.api.modules.user.dto.UserDTO;
import com.chat.api.modules.user.dto.UserFilterDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.modules.user.services.interfaces.IUserService;
import com.chat.api.utils.mapper.user.UserMapper;
import com.chat.api.utils.res.ResponseHttp;
import com.chat.api.utils.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController implements IUserControllerDocs {

    private final IUserService service;
    private final UserMapper mapper;

    @Override
    public ResponseEntity<?> getById(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        UserDTO dto = this.mapper.toDto(principal.getUser());

        var response = ResponseHttp.success(dto, "User found");

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        this.service.delete(principal.getUser());

        var response = ResponseHttp.success(null, "User deleted");

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> update(
            @RequestBody UpdateUserDTO dto,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Result<UserModel> result = this.service.update(principal.getUser(), dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatus())
                    .body(ResponseHttp.error("Error the update user", result.getErrors()));
        }

        UserDTO userDTO = this.mapper.toDto(result.getValue());

        return ResponseEntity
                .ok(ResponseHttp.success(userDTO, "User updated"));
    }

    @Override
    public ResponseEntity<?> findAll(
            @Valid UserFilterDTO filter,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserModel> page = this.service.findAll(filter, pageable);

        Page<UserDTO> pageDto = page.map(mapper::toDto);

        return ResponseEntity.ok(
                pageDto
        );
    }

}
