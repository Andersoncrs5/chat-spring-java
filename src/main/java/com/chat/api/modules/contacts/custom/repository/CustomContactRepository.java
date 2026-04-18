package com.chat.api.modules.contacts.custom.repository;

import com.chat.api.modules.contacts.dto.ContactFilterDTO;
import com.chat.api.modules.contacts.model.ContactModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomContactRepository {
    Page<ContactModel> findFiltered(UUID ownerId, ContactFilterDTO filter, Pageable pageable);
    boolean deleteByID(UUID id);
}
