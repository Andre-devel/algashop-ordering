package com.algaworks.algashop.ordering.domain.model.product;

import com.algaworks.algashop.ordering.domain.model.FieldValidations;

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
