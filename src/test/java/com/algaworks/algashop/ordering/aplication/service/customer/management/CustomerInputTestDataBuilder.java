package com.algaworks.algashop.ordering.aplication.service.customer.management;

import com.algaworks.algashop.ordering.aplication.commons.AddressData;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerInput;

import java.time.LocalDate;

public class CustomerInputTestDataBuilder {
    
    public static CustomerInput.CustomerInputBuilder aCustomerInput() {
        return CustomerInput.builder()
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1991, 7, 21))
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
                        .build());
    }
}
