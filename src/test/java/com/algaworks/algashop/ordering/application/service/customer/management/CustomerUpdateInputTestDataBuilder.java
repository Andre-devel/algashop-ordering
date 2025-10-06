package com.algaworks.algashop.ordering.application.service.customer.management;

import com.algaworks.algashop.ordering.application.commons.AddressData;
import com.algaworks.algashop.ordering.application.customer.management.CustomerUpdateInput;

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
