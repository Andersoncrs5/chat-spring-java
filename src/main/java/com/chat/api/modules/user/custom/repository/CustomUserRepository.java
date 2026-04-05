package com.chat.api.modules.user.custom.repository;

import com.chat.api.modules.user.dto.UserFilterDTO;
import com.chat.api.modules.user.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserRepository {
    Page<UserModel> findByFilter(UserFilterDTO filter, Pageable pageable);
}