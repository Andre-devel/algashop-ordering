package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.validator.FieldValidations;

public record Email(String value) {
    public Email(String value) {
        FieldValidations.requireValidEmail(value);
        
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}
