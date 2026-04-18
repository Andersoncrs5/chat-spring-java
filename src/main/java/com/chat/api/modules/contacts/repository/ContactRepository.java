package com.chat.api.modules.contacts.repository;

import com.chat.api.modules.contacts.custom.repository.CustomContactRepository;
import com.chat.api.modules.contacts.model.ContactModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ContactRepository extends MongoRepository<ContactModel, UUID>, CustomContactRepository {

}
