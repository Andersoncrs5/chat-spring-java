package com.chat.api.configs.mapstruct;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class DateMapper {

    public Instant toInstant(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.toInstant(ZoneOffset.UTC);
    }

    public LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) return null;

        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }
}