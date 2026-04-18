package com.chat.api.modules.contacts.services.interfaces;

import com.chat.api.modules.contacts.dto.ContactFilterDTO;
import com.chat.api.modules.contacts.dto.UpdateContactDTO;
import com.chat.api.modules.contacts.model.ContactModel;
import com.chat.api.utils.annotation.global.isModelInitialized.IsModelInitialized;
import com.chat.api.utils.result.Result;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IContactService {
    Result<ContactModel> findById(@NotNull UUID id);
    void delete(@IsModelInitialized ContactModel contact);
    Result<ContactModel> create(
            @NotNull UUID ownerId,
            @NotNull UUID contactId,
            @NotBlank String nickname
    );
    Page<ContactModel> findAll(
            UUID ownerId,
            ContactFilterDTO filter,
            Pageable pageable
    );
    Result<Void> deleteById(@NotNull UUID id);
    Result<ContactModel> update(@NotNull UUID id, UpdateContactDTO dto);
}
