package com.chat.api.utils.base.repository;

import com.chat.api.utils.base.models.BaseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface CustomBaseRepository<T extends BaseModel> {
    Page<T> findByFilter(Map<String, Object> filters, Pageable pageable, Class<T> entityClass);
}