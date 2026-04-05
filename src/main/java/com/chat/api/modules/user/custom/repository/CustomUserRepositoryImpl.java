package com.chat.api.modules.user.custom.repository;

import com.chat.api.modules.user.dto.UserFilterDTO;
import com.chat.api.modules.user.model.UserModel;
import com.chat.api.utils.base.repository.AbstractCustomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl extends AbstractCustomRepository<UserModel> implements CustomUserRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<UserModel> findByFilter(UserFilterDTO filter, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        if (filter.name() != null && !filter.name().isBlank()) {
            criteria.add(Criteria.where("name").regex(filter.name(), "i"));
        }

        if (filter.username() != null && !filter.username().isBlank()) {
            criteria.add(Criteria.where("username").is(filter.username().toLowerCase()));
        }

        if (filter.email() != null && !filter.email().isBlank()) {
            criteria.add(Criteria.where("email").is(filter.email().toLowerCase()));
        }

        if (filter.roles() != null && !filter.roles().isEmpty()) {
            criteria.add(Criteria.where("roles").in(filter.roles()));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        return execQuery(mongoTemplate, query, pageable, UserModel.class);
    }
}