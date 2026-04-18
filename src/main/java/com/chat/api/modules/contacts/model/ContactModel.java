package com.chat.api.modules.contacts.model;

import com.chat.api.utils.base.models.BaseModel;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

@Document(collection = "contacts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder(toBuilder = true)
@CompoundIndex(name = "user_contact_idx", def = "{'ownerId': 1, 'contactId': 1}", unique = true)
public class ContactModel extends BaseModel {

    @Field("owner_id")
    private UUID ownerId;

    @Field("contact_id")
    private UUID contactId;

    @Field("nickname")
    @Indexed(unique = true)
    private String nickname;

}