package com.chat.api.models;

import com.chat.api.utils.base.models.BaseModel;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.Indexed;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("roles")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RoleModel extends BaseModel {

    @Indexed
    @Column("name")
    private String name;

}
