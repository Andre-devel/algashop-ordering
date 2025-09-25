package com.algaworks.algashop.ordering.domain.model.commons;

import java.util.Objects;

public record Document(String value) {
    
    public Document(String value) {
        Objects.requireNonNull(value);
        
        if (value.isBlank()) {
            throw new IllegalArgumentException("Document cannot be blank");
        }
        
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}
