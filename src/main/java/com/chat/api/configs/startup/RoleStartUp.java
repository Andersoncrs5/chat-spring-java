package com.chat.api.configs.startup;

import com.chat.api.configs.parameters.RoleParameter;
import com.chat.api.modules.role.model.RoleModel;
import com.chat.api.modules.role.services.interfaces.IRoleService;
import com.chat.api.utils.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class RoleStartUp implements CommandLineRunner {

    private final IRoleService roleService;
    private final RoleParameter roleParameter;

    @Override
    public void run(String @NonNull ... args) {
        log.info("Initting verification of roles default...");
        roleParameter.role().roles().forEach(this::createRole);
    }

    private void createRole(String name) {
        Result<RoleModel> result = this.roleService.create(name);

        if (result.isSuccess()) {
            log.info("[STARTUP] Role '{}' created with success.", name);
        } else if (result.getStatus() == HttpStatus.CONFLICT) {
            log.debug("[STARTUP] Role '{}' already exists in db.", name);
        } else {
            log.error("[STARTUP] Fail critical the to create role '{}': {}", name, result.getErrors());
        }
    }
}
