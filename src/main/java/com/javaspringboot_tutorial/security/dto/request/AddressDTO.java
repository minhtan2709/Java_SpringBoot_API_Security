package com.javaspringboot_tutorial.security.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO implements Serializable {

    private String apartmentNumber;

    private String floor;

    private String building;

    private String streetNumber;

    @NotBlank(message = "Street must be not blank")
    private String street;

    @NotBlank(message = "City must be not blank")
    private String city;

    @NotBlank(message = "Country must be not blank")
    private String country;

    @NotNull(message = "Address type must be not null")
    private Integer addressType;

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAddressType(Integer addressType) {
        this.addressType = addressType;
    }
}