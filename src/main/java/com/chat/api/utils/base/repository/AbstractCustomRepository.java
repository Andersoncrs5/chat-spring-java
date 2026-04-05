package com.chat.api.utils.base.repository;

import com.chat.api.utils.base.models.BaseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractCustomRepository<T extends BaseModel> {

    protected List<Criteria> getBaseCriteria(Map<String, Object> commonFilters) {
        List<Criteria> criteria = new ArrayList<>();

        if (commonFilters.containsKey("id") && commonFilters.get("id") != null) {
            criteria.add(Criteria.where("_id").is(commonFilters.get("id")));
        }

        if (commonFilters.containsKey("created_at") && commonFilters.get("created_at") instanceof Instant start) {
            criteria.add(Criteria.where("created_at").gte(start));
        }

        if (commonFilters.containsKey("updated_at") && commonFilters.get("updated_at") instanceof Instant start) {
            criteria.add(Criteria.where("updated_at").gte(start));
        }

        return criteria;
    }

    protected Page<T> execQuery(MongoTemplate mongoTemplate, Query query, Pageable pageable, Class<T> clazz) {
        List<T> list = mongoTemplate.find(query.with(pageable), clazz);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), clazz);
        return PageableExecutionUtils.getPage(list, pageable, () -> count);
    }
}
