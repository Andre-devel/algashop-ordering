package com.algaworks.algashop.ordering.domain.model.customer;

import java.time.LocalDate;
import java.util.Objects;


public record BirthDate(LocalDate value) {
    public BirthDate {
        Objects.requireNonNull(value);

        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }

    }
    
    public Integer age() {
        return LocalDate.now().getYear() - value.getYear();
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}
