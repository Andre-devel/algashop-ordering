package com.algaworks.algashop.ordering.domain.model.commons;

import java.util.Objects;

public record Quantity(Integer value) implements Comparable<Quantity> {
    
    public static Quantity ZERO = new Quantity(0);
    
    public Quantity(Integer value) {
        Objects.requireNonNull(value);
        
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        
        this.value = value;
    }
    
    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    @Override
    public int compareTo(Quantity o) {
        return this.value.compareTo(o.value);
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
}
