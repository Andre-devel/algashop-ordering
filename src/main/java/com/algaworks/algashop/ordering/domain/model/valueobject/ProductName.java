package com.algaworks.algashop.ordering.domain.model.valueobject;

import com.algaworks.algashop.ordering.domain.model.validator.FieldValidations;

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
