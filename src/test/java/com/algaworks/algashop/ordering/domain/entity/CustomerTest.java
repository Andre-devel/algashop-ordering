package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.Email;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.valueobject.ZipCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    Customer.brandNew()
                            .fullName(new FullName("John", "Doe"))
                            .birthDate(new BirthDate(LocalDate.of(1990, 1, 1)))
                            .email(new Email("invalid"))
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
                                    .build())
                            .build();
                });
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomer_shouldGenerateException() {

        Customer customer = 
                Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 1, 1)))
                .email(new Email("invalid"))
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
                        .build())
                .build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.changeEmail("invalid");
                });
    }
    
    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
        Customer customer = Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 1, 1)))
                .email(new Email("invalid"))
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
                        .build())
                .build();
        
        customer.archive();
        
        Assertions.assertWith(customer, c ->  Assertions.assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
                c -> Assertions.assertThat(c.email()).isNotEqualTo(new Email("john.due@gmail.com")),
                c -> Assertions.assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
                c -> Assertions.assertThat(c.document()).isEqualTo(new Document("000-00-0000")),
                c -> Assertions.assertThat(c.birthDate()).isNull(),
                c -> Assertions.assertThat(c.isPromotionNotificationsAllowed()).isFalse(),
                c -> Assertions.assertThat(c.address()).isEqualTo(
                        Address.builder()
                                .street("123 Main St")
                                .complement(null)
                                .neighborhood("Downtown")
                                .number("Anonymized")
                                .city("Metropolis")
                                .state("NY")
                                .zipCode(new ZipCode("12345"))
                                .build())
                );
    }
    
    @Test
    void given_archivedCustomer_whenTryToUpdate_shouldGenerateException() {
        Customer customer = Customer.existing()
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
                        .build()).build();

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);
        
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changeName(new FullName("new", "name"));
                });

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changeEmail("email@gmail.com");
                });

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changePhone("123-456-7890");
                });
        
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::enablePromotionNotifications);
        
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::disablePromotionNotifications);
        
    }

    @Test
    void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
        Customer customer = Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 1, 1)))
                .email(new Email("invalid"))
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
                        .build())
                .build();

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));
        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
        Customer customer = Customer.brandNew()
                .fullName(new FullName("John", "Doe"))
                .birthDate(new BirthDate(LocalDate.of(1990, 1, 1)))
                .email(new Email("invalid"))
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
                        .build())
                .build();

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints()));
        
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> customer.addLoyaltyPoints(new LoyaltyPoints(-10)));
    }
}