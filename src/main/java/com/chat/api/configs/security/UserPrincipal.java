package com.chat.api.configs.security;

import com.chat.api.modules.user.model.UserModel;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

@Getter
public class UserPrincipal extends User {
    private final UUID id;
    private final UserModel user;

    public UserPrincipal(
            UUID id,
            String username,
            @Nullable String password,
            Collection<? extends GrantedAuthority> authorities,
            UserModel user
    ) {
        super(username, password, authorities);
        this.id = id;
        this.user = user;
    }
}
