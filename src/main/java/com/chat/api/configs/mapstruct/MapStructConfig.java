package com.chat.api.configs.mapstruct;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.MapperConfig;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(componentModel = "spring",
        uses = { DateMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public class MapStructConfig {
}
