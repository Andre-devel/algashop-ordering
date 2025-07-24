package com.algaworks.algashop.ordering.domain.valueobject;

import com.algaworks.algashop.ordering.domain.validator.FieldValidations;

public record ProductName(String value) {
    
    public ProductName(String value) {
        FieldValidations.requireNotBlank(value);
        
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}
