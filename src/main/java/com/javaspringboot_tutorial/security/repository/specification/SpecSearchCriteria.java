package com.javaspringboot_tutorial.security.repository.specification;

import lombok.Getter;

import static com.javaspringboot_tutorial.security.repository.specification.SearchOperation.OR_PREDICATE_FLAG;
import static com.javaspringboot_tutorial.security.repository.specification.SearchOperation.ZERO_OR_MORE_REGEX;

@Getter
public class SpecSearchCriteria {

    private String key;
    private SearchOperation operation;
    private Object value;
    private Boolean orPredicate;

    public SpecSearchCriteria(String key, SearchOperation operation, Object value) {
        super();
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    public SpecSearchCriteria(String orPredicate, String key, SearchOperation operation, Object value) {
        super();
        this.orPredicate = orPredicate != null && orPredicate.equals(OR_PREDICATE_FLAG);
        this.key = key;
        this.operation = operation;
        this.value = value;

    }

    public SpecSearchCriteria(String key, String operation, String value, String prefix, String suffix){
        SearchOperation oper = SearchOperation.getSimpleOperation(operation.charAt(0));
        if(oper == SearchOperation.EQUALITY){

            boolean startWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);
            boolean endWithAsterisk = prefix != null && prefix.contains(ZERO_OR_MORE_REGEX);

            if(startWithAsterisk && endWithAsterisk ){
                oper = SearchOperation.CONTAINS;
            } else if (startWithAsterisk) {
                oper = SearchOperation.ENDS_WITH;// khi bat dau bang dau *
            } else if (endWithAsterisk) {
                oper = SearchOperation.STARTS_WITH; // khi ket thuc bang dau *
            }
        }
        this.key = key;
        this.operation = oper;
        this.value = value;
    }
}
