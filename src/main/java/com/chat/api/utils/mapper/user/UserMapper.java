package com.chat.api.utils.mapper.user;

import com.chat.api.configs.mapstruct.DateMapper;
import com.chat.api.modules.user.dto.CreateUserDTO;
import com.chat.api.modules.user.dto.UpdateUserDTO;
import com.chat.api.modules.user.dto.UserDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.utils.annotation.global.isModelInitialized.IsModelInitialized;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { DateMapper.class }
)
public interface UserMapper {

    UserDTO toDto(@IsModelInitialized UserModel model);

    UserModel toModel(CreateUserDTO dto);

    void updateModelFromDto(
            UpdateUserDTO dto,
            @MappingTarget @IsModelInitialized UserModel model
    );
}