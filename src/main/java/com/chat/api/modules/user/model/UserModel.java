package com.chat.api.modules.user.model;

import com.chat.api.utils.base.models.BaseModel;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@Getter
@Setter(AccessLevel.PROTECTED)
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserModel extends BaseModel {

    @Field("name")
    private String name;

    @Indexed(unique = true)
    @Field("username")
    private String username;

    @Field("banner_url")
    private String bannerUrl;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @Field("password")
    private String password;

    @Field("refresh_token")
    private String refreshToken;

    @Field("phone_number")
    private String phoneNumber;

    @Field("login_block_at")
    private OffsetDateTime loginBlockAt;

    @Field("attempts_login")
    private int attemptsLogin = 0;

    @Builder.Default
    @Field("roles")
    private Set<String> roles = new HashSet<>();

    public void addRole(@NotBlank String role) {
        this.roles.add(role.toUpperCase());
    }

    public void removeRole(@NotBlank String role) {
        this.roles.remove(role.toUpperCase());
    }

    public void updatePassword(@NotBlank String newEncodedPassword) {
        this.password = newEncodedPassword;
    }

    public boolean isLoginBlocked() {
        if (loginBlockAt == null) return false;
        return loginBlockAt.isAfter(OffsetDateTime.now());
    }

    public void recordFailedLogin() {
        this.attemptsLogin++;
        if (this.attemptsLogin >= 5) {
            this.loginBlockAt = OffsetDateTime.now().plusMinutes(15);
        }
    }

    public void resetLoginAttempts() {
        this.attemptsLogin = 0;
        this.loginBlockAt = null;
    }

}