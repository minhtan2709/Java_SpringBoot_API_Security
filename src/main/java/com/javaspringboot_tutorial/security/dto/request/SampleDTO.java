package com.javaspringboot_tutorial.security.dto.request;

import lombok.*;

import java.io.Serializable;

//Lombok
@Getter
@Setter
public class SampleDTO implements Serializable {

    private Integer id;
    @NonNull
    private String name;

}
// Maven(top-right) -> M file -> Lifecycle -> compile
