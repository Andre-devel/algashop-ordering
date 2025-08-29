package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import com.algaworks.algashop.ordering.domain.model.utility.IdGenerator;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity.OrderPersistenceEntityBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.embeddable.AddressEmbeddable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public class CustomerPersistenceEntityTestDataBuilder {
    
    private CustomerPersistenceEntityTestDataBuilder() {
    }
    
    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder existingCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(IdGenerator.generateTimeBasedUUID())
                .firstName("John")
                .lastName("Doe")
                .birthDate(java.time.LocalDate.of(1990, 1, 1))
                .email("test@test.com")
                .phone("123-111-9911")
                .document("225-09-1992")
                .promotionNotificationsAllowed(true)
                .archived(false)
                .registeredAt(OffsetDateTime.now())
                .archivedAt(null)
                .loyaltyPoints(0)
                .address(addressBuilder().build());
    }
    
    public static AddressEmbeddable.AddressEmbeddableBuilder addressBuilder() {
        return AddressEmbeddable.builder()
                .street("123 Main St")
                .complement("Apt 4B")
                .neighborhood("Downtown")
                .number("123")
                .city("Metropolis")
                .state("NY")
                .zipCode("12345");
    }
}
