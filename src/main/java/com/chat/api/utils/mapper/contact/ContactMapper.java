package com.chat.api.utils.mapper.contact;

import com.chat.api.configs.mapstruct.DateMapper;
import com.chat.api.modules.contacts.model.ContactModel;
import com.chat.api.modules.contacts.dto.ContactDTO;
import com.chat.api.modules.contacts.dto.CreateContactDTO;
import com.chat.api.modules.contacts.dto.UpdateContactDTO;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { DateMapper.class }
)
public interface ContactMapper {

    ContactDTO toDto(ContactModel model);

    ContactModel toModel(CreateContactDTO dto);

    void merge(
            UpdateContactDTO dto,
            @MappingTarget ContactModel model
    );
}