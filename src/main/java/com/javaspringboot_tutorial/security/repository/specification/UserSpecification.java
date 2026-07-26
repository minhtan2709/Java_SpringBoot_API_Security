package com.javaspringboot_tutorial.security.repository.specification;

import com.javaspringboot_tutorial.security.model.User;
import com.javaspringboot_tutorial.security.util.Gender;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
public class UserSpecification implements Specification<User> {

    private SpecSearchCriteria criteria;


    @Override
    public Predicate toPredicate(@NonNull Root<User> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {
        String valueStr = criteria.getValue().toString().trim();
        String key = criteria.getKey().trim();
        return switch (criteria.getOperation()) {
            case EQUALITY -> {
                // Neu truong tim kiem la gender thi moi chuyen sang Enum Gender
                if ("gender".equalsIgnoreCase(key)) {
                    yield builder.equal(root.get(key), Gender.valueOf(valueStr.toUpperCase()));
                }
                // Nguoc lai cac truong khac (nhu username, email...) thi so sanh String thong thuong
                yield builder.equal(root.get(key), criteria.getValue());
            }
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString()+"%");
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue().toString() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString());
            case CONTAINS -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue().toString() + "%");
        };
    }
}
