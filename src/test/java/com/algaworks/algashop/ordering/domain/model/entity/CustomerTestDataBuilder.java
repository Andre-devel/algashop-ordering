package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class CustomerTestDataBuilder {
    
    private CustomerTestDataBuilder() {}
    
    public static Customer.BrandNewCustomerBuilder brandNewCustomer() {
        return Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 1, 1)))
                .email(new Email("john.due@gmail.com"))
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
                .id(new CustomerId())     
                .registeredAt(OffsetDateTime.now())
                .promotionNotificationsAllowed(true)
                .archived(false)
                .archivedAt(null)
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 1, 1)))
                .email(new Email("john.due@gmail.com"))
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
