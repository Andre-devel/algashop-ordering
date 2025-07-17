package com.algaworks.algashop.ordering.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;


public record BirthDate(LocalDate value) {
    public BirthDate(LocalDate value) {
        Objects.requireNonNull(value);    
        
        if (value.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birth date cannot be in the future");
        }
        
        this.value = value;
    }
    
    public Integer age() {
        return LocalDate.now().getYear() - value.getYear();
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}
