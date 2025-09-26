package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CustomerTest {

    @Test
    void given_invalidEmail_whenTryCreateCustomer_shouldGenerateException() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    CustomerTestDataBuilder.brandNewCustomer().email(new Email("invalid")).build();
                });
    }

    @Test
    void given_invalidEmail_whenTryUpdateCustomer_shouldGenerateException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();
        
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    customer.changeEmail(new Email("invalid"));
                });
    }
    
    @Test
    void given_unarchivedCustomer_whenArchive_shouldAnonymize() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        
        customer.archive();
        
        Assertions.assertWith(customer, c ->  Assertions.assertThat(c.fullName()).isEqualTo(new FullName("Anonymous", "Anonymous")),
                c -> Assertions.assertThat(c.email()).isNotEqualTo(new Email("john.due@gmail.com")),
                c -> Assertions.assertThat(c.phone()).isEqualTo(new Phone("000-000-0000")),
                c -> Assertions.assertThat(c.document()).isEqualTo(new Document("000-00-0000")),
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
        Customer customer = CustomerTestDataBuilder.existingAnonymizedCustomer().build();


        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::archive);
        
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changeName(new FullName("new", "name"));
                });

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changeEmail(new Email("email@gmail.com"));
                });

        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(() -> {
                    customer.changePhone(new Phone("123-456-7890"));
                });
        
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::enablePromotionNotifications);
        
        Assertions.assertThatExceptionOfType(CustomerArchivedException.class)
                .isThrownBy(customer::disablePromotionNotifications);
        
    }

    @Test
    void given_brandNewCustomer_whenAddLoyaltyPoints_shouldSumPoints() {
        Customer customer =CustomerTestDataBuilder.brandNewCustomer().build();

        customer.addLoyaltyPoints(new LoyaltyPoints(10));
        customer.addLoyaltyPoints(new LoyaltyPoints(20));
        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }

    @Test
    void given_brandNewCustomer_whenAddInvalidLoyaltyPoints_shouldGenerateException() {
        Customer customer = CustomerTestDataBuilder.brandNewCustomer().build();

        Assertions.assertThatNoException() //alterar
                .isThrownBy(()-> customer.addLoyaltyPoints(new LoyaltyPoints(0)));

        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> new LoyaltyPoints(-10));
    }
}