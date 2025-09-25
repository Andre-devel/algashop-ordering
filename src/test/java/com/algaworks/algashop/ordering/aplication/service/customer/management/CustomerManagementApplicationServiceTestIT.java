package com.algaworks.algashop.ordering.aplication.service.customer.management;

import com.algaworks.algashop.ordering.aplication.commons.AddressData;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerOutput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
class CustomerManagementApplicationServiceTestIT {
    
    @Autowired
    private CustomerManagementApplicationService service;
    
    @Test
    public void shouldRegister() {
        CustomerInput input = CustomerInput.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1991,7,21))
                .document("123-42-1232")
                .phone("478-256-2362")
                .email("jonfo@email.com")
                .promotionNotificationsAllowed(false)
                .address(AddressData.builder()
                        .street("123 Main St")
                        .number("456")
                        .complement("Apt 789")
                        .neighborhood("Downtown")
                        .city("Springfield")
                        .state("IL")
                        .zipCode("62704")
                        .build())
                .build();
        UUID customerId = service.create(input);
        Assertions.assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = service.findById(customerId);
        
        Assertions.assertThat(customerOutput.getId()).isEqualTo(customerId);
        Assertions.assertThat(customerOutput.getFirstName()).isEqualTo("John");
        Assertions.assertThat(customerOutput.getLastName()).isEqualTo("Doe");
        Assertions.assertThat(customerOutput.getEmail()).isEqualTo("jonfo@email.com");
        Assertions.assertThat(customerOutput.getBirthDate()).isEqualTo(LocalDate.of(1991,7,21));
        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }
}