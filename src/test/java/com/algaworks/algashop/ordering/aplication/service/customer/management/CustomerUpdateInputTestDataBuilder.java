package com.algaworks.algashop.ordering.aplication.service.customer.management;

import com.algaworks.algashop.ordering.aplication.commons.AddressData;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerUpdateInput;

public class CustomerUpdateInputTestDataBuilder {
    
    public static CustomerUpdateInput.CustomerUpdateInputBuilder aCustomerUpdateInput() {
        return CustomerUpdateInput.builder()
                .firstName("maria")
                .lastName("silva")
                .phone("11999999999")
                .promotionNotificationsAllowed(true)
                .address(AddressData.builder()
                        .street("Amphitheatre Parkway")
                        .number("1600")
                        .complement("")
                        .neighborhood("Mountain View")
                        .city("CA")
                        .state("California")
                        .zipCode("94043")
                        .build());
    }
}
