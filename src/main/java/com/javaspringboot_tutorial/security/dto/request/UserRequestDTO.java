package com.javaspringboot_tutorial.security.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.javaspringboot_tutorial.security.dto.validator.EnumValue;
import com.javaspringboot_tutorial.security.dto.validator.PhoneNumber;
import com.javaspringboot_tutorial.security.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class UserRequestDTO implements Serializable {
    //chuyen doi tu Json sang bytes nhi phan
    @NotBlank(message = "firstName must be not blank") //blank la khoang trang
    private String firstName;

    @NotNull(message = "lastName must be not null") //null la khong gia tri
    private String lastName;

     //@Pattern(message = "phone invalid format", regexp = "\\d{10}$") pattern chi truong gia tri chuoi hop le khi khop voi bieu thuc duy nhat
    @PhoneNumber(message = "phone invalid format")
    private String phone;

    @Email(message = "email invalid format")
    private String email;

    @NotNull(message = "date must be not null")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy", lenient = OptBoolean.FALSE)
    private Date dateOfBirth;

//    @Pattern(regexp = "^ACTIVE|INACTIVE|NONE$", message = "status must be one in {ACTIVE, INACTIVE, NONE}")//regexp regular expression
    private UserStatus status;

    private Gender gender;

    @NotNull(message = "type must be not null")
    @EnumValue(name = "type", enumClass = UserType.class)
    private String type;

    @NotNull(message = "username must be not null")
    private String userName;

    @NotNull(message = "password must be not null")
    private String password;

    @NotEmpty(message = "Address can not empty")
    private Set<AddressDTO> addresses;

    public Set<AddressDTO> getAddresses() {
        return addresses;
    }

    public UserRequestDTO(String firstName, String lastName, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }


}

