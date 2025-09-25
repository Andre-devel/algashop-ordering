package com.algaworks.algashop.ordering.domain.model.customer;

import static com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;

import java.time.OffsetDateTime;

public class CustomerPersistenceEntityTestDataBuilder {
    
    private CustomerPersistenceEntityTestDataBuilder() {
    }
    
    public static CustomerPersistenceEntity.CustomerPersistenceEntityBuilder existingCustomer() {
        return CustomerPersistenceEntity.builder()
                .id(DEFAULT_CUSTOMER_ID.value())
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
