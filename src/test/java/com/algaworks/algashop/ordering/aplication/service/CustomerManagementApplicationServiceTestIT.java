package com.algaworks.algashop.ordering.aplication.service;

import com.algaworks.algashop.ordering.aplication.model.AddressData;
import com.algaworks.algashop.ordering.aplication.model.CustomerInput;
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
    }
}