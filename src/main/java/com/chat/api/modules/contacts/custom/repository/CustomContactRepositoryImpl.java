package com.chat.api.modules.contacts.custom.repository;

import com.chat.api.modules.contacts.dto.ContactFilterDTO;
import com.chat.api.modules.contacts.model.ContactModel;
import com.chat.api.utils.base.repository.AbstractCustomRepository;
import com.mongodb.client.result.DeleteResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CustomContactRepositoryImpl extends AbstractCustomRepository<ContactModel> implements CustomContactRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ContactModel> findFiltered(UUID ownerId, ContactFilterDTO filter, Pageable pageable) {
        Query query = new Query().with(pageable);

        query.addCriteria(Criteria.where("ownerId").is(ownerId));

        if (filter.nickname() != null && !filter.nickname().isBlank()) {
            query.addCriteria(Criteria.where("nickname").regex(filter.nickname(), "i"));
        }

        if (filter.contactId() != null) {
            query.addCriteria(Criteria.where("contactId").is(filter.contactId()));
        }

        long total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ContactModel.class);
        List<ContactModel> list = mongoTemplate.find(query, ContactModel.class);

        return PageableExecutionUtils.getPage(list, pageable, () -> total);
    }

    @Override
    public boolean deleteByID(UUID id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));

        DeleteResult removed = mongoTemplate.remove(query, ContactModel.class);

        return removed.getDeletedCount() > 0;
    }
}