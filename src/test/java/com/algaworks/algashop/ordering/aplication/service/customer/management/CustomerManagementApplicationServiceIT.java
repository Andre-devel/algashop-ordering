package com.algaworks.algashop.ordering.aplication.service.customer.management;

import com.algaworks.algashop.ordering.aplication.customer.management.CustomerInput;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerManagementApplicationService;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerOutput;
import com.algaworks.algashop.ordering.aplication.customer.management.CustomerUpdateInput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerEmailIsInUseException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
@Transactional
class CustomerManagementApplicationServiceIT {
    
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
    
    @Test
    public void shouldArchive() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomerInput().build();
        UUID customerId = service.create(input);
        
        Assertions.assertThat(customerId).isNotNull();
        
        service.archive(customerId);
        
        CustomerOutput customerOutput = service.findById(customerId);
        
        Assertions.assertThat(customerOutput.getArchived()).isTrue();
        Assertions.assertThat(customerOutput.getArchivedAt()).isNotNull();

        Assertions.assertThat(customerOutput.getEmail()).isNotEqualTo(input.getEmail());
        
        Assertions.assertThat(customerOutput).extracting(
                CustomerOutput::getId,
                CustomerOutput::getArchived,
                CustomerOutput::getFirstName,
                CustomerOutput::getLastName,
                CustomerOutput::getPhone,
                CustomerOutput::getDocument,
                CustomerOutput::getPromotionNotificationsAllowed,
                CustomerOutput::getBirthDate,
                cutomer -> cutomer.getAddress().getCity(),
                cutomer -> cutomer.getAddress().getState(),
                cutomer -> cutomer.getAddress().getZipCode(),
                cutomer -> cutomer.getAddress().getStreet(),
                cutomer -> cutomer.getAddress().getNumber(),
                cutomer -> cutomer.getAddress().getComplement()
        ).containsExactly(
                customerId,
                true,
                "Anonymous",
                "Anonymous",
                "000-000-0000",
                "000-00-0000",
                false,
                null,
                "Anonymized",
                "AN",
                "00000",
                "Anonymized",
                "0",
                "Anonymized"
        );
    }
    
    @Test
    void shouldNotArchiveWhenCustomerNotExists() {
        UUID customerId = UUID.randomUUID();
        
        Assertions.assertThatThrownBy(() -> service.archive(customerId))
                .isInstanceOf(CustomerNotFoundException.class);
    }
    
    @Test
    void shouldNotArchiveWhenCustomerAlreadyArchived() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomerInput().build();
        UUID customerId = service.create(input);
        service.archive(customerId);
        
        Assertions.assertThatThrownBy(() -> service.archive(customerId))
                .isInstanceOf(CustomerArchivedException.class);
    }
    
    @Test
    void shouldChangeEmail() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomerInput().build();
        UUID customerId = service.create(input);
        
        service.changeEmail(customerId, "newemail@email.com");
        CustomerOutput customerOutput = service.findById(customerId);
        
        Assertions.assertThat(customerOutput.getEmail()).isEqualTo("newemail@email.com");
    }
    
    @Test
    void shouldNotChangeEmailWhenCustomerNotExists() {
        UUID customerId = UUID.randomUUID();
        
        Assertions.assertThatThrownBy(() -> service.changeEmail(customerId, "newemail@email.com"))
                .isInstanceOf(CustomerNotFoundException.class);
    }
    
    @Test
    void shouldNotChangeEmailWhenCustomerIsArchived() {
        CustomerInput input = CustomerInputTestDataBuilder.aCustomerInput().build();
        UUID customerId = service.create(input);
        service.archive(customerId);

        Assertions.assertThatThrownBy(() -> service.changeEmail(customerId, "newemail@email.com"))
                .isInstanceOf(CustomerArchivedException.class);
    }
    
    @Test
    void shouldNotChangeEmailWhenNewEmailIsInUse() {
        CustomerInput input1 = CustomerInputTestDataBuilder.aCustomerInput().build();
        CustomerInput input2 = CustomerInputTestDataBuilder.aCustomerInput().email("existsemail@email.com").build();

        UUID customerId1 = service.create(input1);
        UUID customerId2 = service.create(input2);

        Assertions.assertThatThrownBy(() -> service.changeEmail(customerId1, "existsemail@email.com"))
                .isInstanceOf(CustomerEmailIsInUseException.class);

    }
}