package com.algaworks.algashop.ordering.domain.model.customer;

import java.time.LocalDate;


public record BirthDate(LocalDate value) {
    public BirthDate {
        if (value != null && value.isAfter(LocalDate.now())) {
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
