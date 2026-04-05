package com.chat.api.utils.base.repository;

import com.chat.api.utils.base.models.BaseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomBaseRepositoryImpl<T extends BaseModel> implements CustomBaseRepository<T> {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<T> findByFilter(Map<String, Object> filters, Pageable pageable, Class<T> entityClass) {
        Query query = new Query().with(pageable);
        List<Criteria> criteriaList = new ArrayList<>();

        filters.forEach((key, value) -> {
            if (value != null) {
                if (value instanceof String str && !str.isBlank()) {

                    criteriaList.add(Criteria.where(key).regex(str, "i"));
                } else if (value instanceof Collection<?> col && !col.isEmpty()) {
                    // Busca 'IN' para listas (ex: roles)
                    criteriaList.add(Criteria.where(key).in(col));
                } else {
                    criteriaList.add(Criteria.where(key).is(value));
                }
            }
        });

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        List<T> results = mongoTemplate.find(query, entityClass);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), entityClass);

        return PageableExecutionUtils.getPage(results, pageable, () -> count);
    }
}