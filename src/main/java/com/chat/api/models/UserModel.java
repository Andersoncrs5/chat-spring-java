package com.chat.api.models;

import com.chat.api.utils.base.models.BaseModel;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.Indexed;
import java.time.OffsetDateTime;
import java.util.Set;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Table("users")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserModel extends BaseModel {

    @Column("name")
    private String name;

    @Indexed
    @Column("username")
    private String username;

    @Column("banner_url")
    private String bannerUrl;

    @Indexed
    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("refresh_token")
    private String refreshToken;

    @Column("phone_number")
    private String phoneNumber;

    @Column("login_block_at")
    private OffsetDateTime loginBlockAt;

    @Column("roles")
    private Set<String> roles;
}