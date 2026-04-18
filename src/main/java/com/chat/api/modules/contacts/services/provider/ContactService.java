package com.chat.api.modules.contacts.services.provider;

import com.chat.api.configs.transactional.ResultTransactional;
import com.chat.api.modules.contacts.dto.ContactFilterDTO;
import com.chat.api.modules.contacts.dto.UpdateContactDTO;
import com.chat.api.modules.contacts.gateway.ContactModuleGateway;
import com.chat.api.modules.contacts.model.ContactModel;
import com.chat.api.modules.contacts.repository.ContactRepository;
import com.chat.api.modules.contacts.services.interfaces.IContactService;
import com.chat.api.utils.annotation.global.isModelInitialized.IsModelInitialized;
import com.chat.api.utils.mapper.contact.ContactMapper;
import com.chat.api.utils.result.Result;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

@Service @Validated
@RequiredArgsConstructor
public class ContactService implements IContactService {

    private final ContactRepository repository;
    private final ContactModuleGateway gateway;
    private final ContactMapper mapper;

    public void delete(@IsModelInitialized ContactModel contact) {
        repository.delete(contact);
    }

    @ResultTransactional
    public Result<Void> deleteById(@NotNull UUID id) {
        boolean deleted = repository.deleteByID(id);

        if (!deleted) {
            return Result.notFound("Contact not found");
        }

        return Result.success(null);
    }

    @ResultTransactional
    public Result<ContactModel> create(
            @NotNull UUID ownerId,
            @NotNull UUID contactId,
            @NotBlank String nickname
    ) {
        if (ownerId.equals(contactId))
            return Result.badRequest("You cannot add yourself as a contact.");

        if (!gateway.existsUserById(contactId).getValue())
            return Result.notFound("The user you are trying to add does not exist.");

        var model = new ContactModel().toBuilder()
                .ownerId(ownerId)
                .contactId(contactId)
                .nickname(nickname)
                .build();

        try {
            return Result.created(repository.save(model));
        } catch (DuplicateKeyException e) {
            return Result.conflict("This contact is already in already.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Result<ContactModel> findById(@NotNull UUID id) {
        Optional<ContactModel> optional = repository.findById(id);

        return optional.map(Result::success).orElseGet(() -> Result.notFound("Contact not found"));
    }

    public Page<ContactModel> findAll(
            UUID ownerId,
            ContactFilterDTO filter,
            Pageable pageable
    ) {
        return repository.findFiltered(ownerId, filter, pageable);
    }

    @ResultTransactional
    public Result<ContactModel> update(@NotNull UUID id, UpdateContactDTO dto) {
        Optional<ContactModel> optional = repository.findById(id);
        ContactModel contact;

        if (optional.isEmpty())
            return Result.notFound("Contact not found");

        contact = optional.get();

        this.mapper.merge(dto, contact);

        return Result.success(repository.save(contact));
    }

}
