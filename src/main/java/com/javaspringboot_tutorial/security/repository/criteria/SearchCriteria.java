package com.javaspringboot_tutorial.security.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {

    private String key; //firstName, lastName, id, email, ...

    private String operation; //=, <, >,...

    private Object value;


}
