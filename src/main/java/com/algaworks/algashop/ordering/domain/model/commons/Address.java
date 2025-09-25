package com.algaworks.algashop.ordering.domain.model.commons;

import com.algaworks.algashop.ordering.domain.model.FieldValidations;
import lombok.Builder;

import java.util.Objects;

@Builder(toBuilder = true)
public record Address(
        String street,
        String complement,
        String neighborhood,
        String number,
        String city,
        String state,
        ZipCode zipCode
) {
    
    public Address {
        FieldValidations.requireNotBlank(street);
        FieldValidations.requireNotBlank(neighborhood);
        FieldValidations.requireNotBlank(number);
        FieldValidations.requireNotBlank(city);
        FieldValidations.requireNotBlank(state);
        Objects.requireNonNull(zipCode);
        
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s - %s", street, complement, neighborhood, number, city, state, zipCode);
    }
}
