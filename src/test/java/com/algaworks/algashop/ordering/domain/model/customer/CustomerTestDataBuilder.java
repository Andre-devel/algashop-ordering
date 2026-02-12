package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerTestDataBuilder {
    
    public static final CustomerId DEFAULT_CUSTOMER_ID = new CustomerId(UUID.fromString("6e148bd5-47f6-4022-b9da-07cfaa294f7a"));
    
    private CustomerTestDataBuilder() {}
    
    public static Customer.BrandNewCustomerBuilder brandNewCustomer() {
        return Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 1, 1)))
                .email(new Email("john_" + UUID.randomUUID() + "@gmail.com"))
                .phone(new Phone("123456789"))
                .document(new Document("12345678901"))
                .promotionNotificationsAllowed(false)
                .address(Address.builder()
                        .street("123 Main St")
                        .complement("Apt 4B")
                        .neighborhood("Downtown")
                        .number("123")
                        .city("Metropolis")
                        .state("NY")
                        .zipCode(new ZipCode("12345"))
                        .build());
    }
    
    public static Customer.ExistingCustomerBuilder existingCustomer() {
        return Customer.existing()
                .id(DEFAULT_CUSTOMER_ID)     
                .registeredAt(OffsetDateTime.now())
                .promotionNotificationsAllowed(true)
                .archived(false)
                .archivedAt(null)
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 1, 1)))
                .email(new Email("john_" + UUID.randomUUID() + "@gmail.com"))
                .phone(new Phone("123456789"))
                .document(new Document("12345678901"))
                .promotionNotificationsAllowed(false)
                .loyaltyPoints(new LoyaltyPoints(0))
                .address(Address.builder()
                        .street("123 Main St")
                        .complement("Apt 4B")
                        .neighborhood("Downtown")
                        .number("123")
                        .city("Metropolis")
                        .state("NY")
                        .zipCode(new ZipCode("12345"))
                        .build());
    };
    
    public static Customer.ExistingCustomerBuilder existingAnonymizedCustomer() {
        return Customer.existing()
                .id(new CustomerId())
                .fullName(new FullName("Anonymous", "Anonymous"))
                .birthDate(null)
                .email(new Email("anonymous@anonymous.com"))
                .phone(new Phone("000-000-0000"))
                .document(new Document("000-00-0000"))
                .promotionNotificationsAllowed(false)
                .archived(true)
                .archivedAt(OffsetDateTime.now())
                .registeredAt(OffsetDateTime.now())
                .loyaltyPoints(new LoyaltyPoints(10))
                .address(
                        Address.builder()
                                .street("123 Main St")
                                .complement("Apt 4B")
                                .neighborhood("Downtown")
                                .number("123")
                                .city("Metropolis")
                                .state("NY")
                                .zipCode(new ZipCode("12345"))
                                .build());
    }
}
