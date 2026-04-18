package com.chat.api.unit.contact;

import com.chat.api.modules.contacts.dto.ContactFilterDTO;
import com.chat.api.modules.contacts.dto.UpdateContactDTO;
import com.chat.api.modules.contacts.gateway.ContactModuleGateway;
import com.chat.api.modules.contacts.model.ContactModel;
import com.chat.api.modules.contacts.repository.ContactRepository;
import com.chat.api.modules.contacts.services.provider.ContactService;
import com.chat.api.utils.mapper.contact.ContactMapper;
import com.chat.api.utils.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContactServiceTest {

    @Mock private ContactMapper mapper;
    @Mock private ContactModuleGateway gateway;
    @Mock private ContactRepository repository;

    @InjectMocks private ContactService service;

    ContactModel contact = new ContactModel().toBuilder()
            .id(UUID.randomUUID())
            .contactId(UUID.randomUUID())
            .ownerId(UUID.randomUUID())
            .nickname("pochita")
            .version(1L)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();


    ContactModel contactUpdated = new ContactModel().toBuilder()
            .id(contact.getId())
            .contactId(contact.getContactId())
            .ownerId(contact.getOwnerId())
            .nickname("pochita-updated")
            .version(1L)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    UpdateContactDTO dto = new UpdateContactDTO(
            contactUpdated.getNickname()
    );

    @Test
    void shouldDelete() {
        doNothing().when(repository).delete(contact);

        this.service.delete(contact);

        verify(repository, times(1)).delete(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnTrueWhenDeleteById() {
        when(repository.deleteByID(any())).thenReturn(true);

        Result<Void> result = this.service.deleteById(this.contact.getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getErrors().size()).isZero();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        verify(repository, times(1)).deleteByID(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnFalseWhenDeleteById() {
        when(repository.deleteByID(any())).thenReturn(false);

        Result<Void> result = this.service.deleteById(this.contact.getId());

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrors().getFirst()).isEqualTo("Contact not found");
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(repository, times(1)).deleteByID(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldCreate() {
        when(gateway.existsUserById(this.contact.getContactId())).thenReturn(Result.success(true));
        when(repository.save(any())).thenReturn(contact);

        Result<ContactModel> result = this.service.create(contact.getOwnerId(), contact.getContactId(), contact.getNickname());
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getId()).isEqualTo(contact.getId());

        verify(gateway, times(1)).existsUserById(this.contact.getContactId());
        verify(repository, times(1)).save(any());

        verifyNoMoreInteractions(repository, gateway);
    }

    @Test
    void shouldReturnBadRequestBecauseOwerIdAreEqual() {
        var id = UUID.randomUUID();
        Result<ContactModel> result = this.service.create(id, id, contact.getNickname());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors().getFirst()).contains("yourself", "contact");
        assertThat(result.getValue()).isNull();

        verify(gateway, never()).existsUserById(this.contact.getContactId());
        verify(repository, never()).save(any());

        verifyNoMoreInteractions(repository, gateway);
    }

    @Test
    void shouldReturnNotFoundBecauseContactNotExists() {
        when(gateway.existsUserById(this.contact.getContactId())).thenReturn(Result.success(false));

        Result<ContactModel> result = this.service.create(contact.getOwnerId(), contact.getContactId(), contact.getNickname());
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getValue()).isNull();

        verify(gateway, times(1)).existsUserById(this.contact.getContactId());
        verify(repository, times(0)).save(any());

        verifyNoMoreInteractions(repository, gateway);
    }

    @Test
    void shouldReturnConflictBecauseContactAlreadyExists() {
        DuplicateKeyException exception = mock(DuplicateKeyException.class);

        when(gateway.existsUserById(this.contact.getContactId())).thenReturn(Result.success(true));
        when(repository.save(any())).thenThrow(exception);

        Result<ContactModel> result = this.service.create(contact.getOwnerId(), contact.getContactId(), contact.getNickname());
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getValue()).isNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(result.getErrors().getFirst()).containsIgnoringCase("contact");

        verify(gateway, times(1)).existsUserById(this.contact.getContactId());
        verify(repository, times(1)).save(any());

        verifyNoMoreInteractions(repository, gateway);
    }

    @Test
    void shouldReturnContactWhenGetById() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.of(contact));

        Result<ContactModel> result = this.service.findById(contact.getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getId()).isEqualTo(contact.getId());
        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);

        verify(repository, times(1)).findById(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnNullWhenGetById() {
        when(repository.findById(any(UUID.class))).thenReturn(Optional.empty());

        Result<ContactModel> result = this.service.findById(contact.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getValue()).isNull();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(repository, times(1)).findById(any());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnPagedContacts() {
        UUID ownerId = UUID.randomUUID();
        ContactFilterDTO filter = new ContactFilterDTO("pochita", null);
        Pageable pageable = PageRequest.of(0, 10);

        List<ContactModel> contactList = List.of(contact);
        Page<ContactModel> contactPage = new PageImpl<>(contactList, pageable, contactList.size());

        when(repository.findFiltered(eq(ownerId), eq(filter), any(Pageable.class)))
                .thenReturn(contactPage);

        Page<ContactModel> result = this.service.findAll(ownerId, filter, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getNickname()).isEqualTo("pochita");
        assertThat(result.getTotalElements()).isEqualTo(1);

        // Verify
        verify(repository, times(1)).findFiltered(ownerId, filter, pageable);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldReturnEmptyPageWhenNoContactsFound() {
        // Arrange
        UUID ownerId = UUID.randomUUID();
        ContactFilterDTO filter = new ContactFilterDTO("nonexistent", null);
        Pageable pageable = PageRequest.of(0, 10);

        Page<ContactModel> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(repository.findFiltered(any(), any(), any()))
                .thenReturn(emptyPage);

        Page<ContactModel> result = this.service.findAll(ownerId, filter, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(repository, times(1)).findFiltered(any(), any(), any());
    }

    @Test
    void shouldReturnContactUpdated() {
        when(repository.findById(contact.getId())).thenReturn(Optional.of(contact));
        doNothing().when(mapper).merge(dto, contact);
        when(repository.save(any())).thenReturn(contactUpdated);

        Result<ContactModel> result = this.service.update(contact.getId(), dto);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(result.getErrors().size()).isZero();
        assertThat(result.isSuccess()).isTrue();

        verify(repository, times(1)).findById(contact.getId());
        verify(repository, times(1)).save(any());
        verify(mapper, times(1)).merge(dto, contact);
        verifyNoMoreInteractions(repository, mapper);

        InOrder order = inOrder(repository, mapper);
        order.verify(repository).findById(contact.getId());
        order.verify(mapper).merge(dto, contact);
        order.verify(repository).save(any());
    }

    @Test
    void shouldReturnNotFoundBecauseUserNotFoundWhenGetByIdInUpdate() {
        when(repository.findById(contact.getId())).thenReturn(Optional.empty());

        Result<ContactModel> result = this.service.update(contact.getId(), dto);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getErrors().size()).isEqualTo(1);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getErrors().getFirst()).isEqualTo("Contact not found");

        verify(repository, times(1)).findById(contact.getId());
        verify(repository, never()).save(any());
        verify(mapper, never()).merge(dto, contact);

        verifyNoMoreInteractions(repository, mapper);
    }

}
