package com.algaworks.algashop.ordering;

import com.algaworks.algashop.ordering.domain.entity.Customer;
import com.algaworks.algashop.ordering.domain.valueobject.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.LoyaltyPoints;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CustomTest {
    
    @Test
    public void testingCustomer() {
        Customer customer = new Customer(
                new CustomerId(),
                new FullName("John", "Doe"),
                LocalDate.of(1990, 1, 1),
                "jhon.tetst@gmail.com",
                "+1234567890",
                "12345678901",
                true,
                OffsetDateTime.now()
        );
        
        customer.addLoyaltyPoints(new LoyaltyPoints(10));
    }
}
