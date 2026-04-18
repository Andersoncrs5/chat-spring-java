package com.chat.api.modules.contacts.gateway;

import com.chat.api.modules.user.services.interfaces.IUserService;
import com.chat.api.utils.result.Result;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ContactModuleGateway {

    private final IUserService userService;

    public Result<Boolean> existsUserById(@NotNull UUID id) {
        return this.userService.existsById(id);
    }

}
