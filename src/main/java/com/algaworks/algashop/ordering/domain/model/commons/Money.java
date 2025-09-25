package com.algaworks.algashop.ordering.domain.model.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal value) implements Comparable<Money>{
    
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    
    public Money(BigDecimal value) {
        Objects.requireNonNull(value);
        
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money value cannot be negative");
        }
        
        this.value = value.setScale(2, RoundingMode.HALF_EVEN);
    }
    
    public Money(String value) {
        this(new BigDecimal(value));
    } 
    
    public Money multiply(Quantity quantity) {
        if (quantity.value() < 1) {
            throw new IllegalArgumentException();
        }
        
        return new Money(this.value.multiply(new BigDecimal(quantity.value())));
    }
    
    public Money add(Money other) {
        return new Money(this.value.add(other.value));
    }
    
    public Money divide(Money other) {
        return new Money(this.value.divide(other.value,RoundingMode.HALF_EVEN));
    }

    @Override
    public int compareTo(Money o) {
        return this.value.compareTo(o.value);
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
}
