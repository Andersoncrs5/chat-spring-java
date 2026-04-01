package com.chat.api.modules.user.model;

import com.chat.api.utils.base.models.BaseModel;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.Set;

@Document(collection = "users")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
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

    @Field("roles")
    private Set<String> roles;
}