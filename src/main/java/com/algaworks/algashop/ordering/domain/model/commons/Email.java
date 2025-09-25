package com.algaworks.algashop.ordering.domain.model.commons;

import com.algaworks.algashop.ordering.domain.model.FieldValidations;

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
