package com.algaworks.algashop.ordering.aplication.service.customer.management;

import com.algaworks.algashop.ordering.aplication.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerOutput;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerUpdateInput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceTestIT {
    
    @Autowired
    private CustomerManagementApplicationService service;
    
    @Test
    public void shouldRegister() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomerInput().build();
        
        UUID customerId = service.create(input);
        Assertions.assertThat(customerId).isNotNull();

        CustomerOutput customerOutput = service.findById(customerId);
        
        Assertions.assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getEmail,
                        CustomerOutput::getBirthDate
                ).containsExactly(
                        customerId,
                        "John",
                        "Doe",
                        "jonfo@email.com",
                        LocalDate.of(1991,7,21)
                );
        
        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }

    @Test
    public void shouldUpdate() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomerInput().build();
        CustomerUpdateInput updateInput = CustomerUpdateInputTestDataBuilder.aCustomerUpdateInput().build();

        UUID customerId = service.create(input);
        Assertions.assertThat(customerId).isNotNull();
        
        service.update(customerId, updateInput);

        CustomerOutput customerOutput = service.findById(customerId);

        Assertions.assertThat(customerOutput)
                .extracting(
                        CustomerOutput::getId,
                        CustomerOutput::getFirstName,
                        CustomerOutput::getLastName,
                        CustomerOutput::getEmail,
                        CustomerOutput::getBirthDate
                ).containsExactly(
                        customerId,
                        "maria",
                        "silva",
                        "jonfo@email.com",
                        LocalDate.of(1991,7,21)
                );

        Assertions.assertThat(customerOutput.getRegisteredAt()).isNotNull();
    }
}