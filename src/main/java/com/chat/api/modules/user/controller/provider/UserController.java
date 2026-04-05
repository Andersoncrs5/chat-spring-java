package com.chat.api.modules.user.controller.provider;

import com.chat.api.modules.user.controller.docs.IUserControllerDocs;
import com.chat.api.modules.user.services.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController implements IUserControllerDocs {

    private final IUserService service;

}
