package com.algaworks.algashop.ordering.aplication.customer.management;

import com.algaworks.algashop.ordering.aplication.commons.AddressData;
import com.algaworks.algashop.ordering.domain.model.commons.Address;
import com.algaworks.algashop.ordering.domain.model.commons.Document;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.commons.Phone;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegistrationService;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerManagementApplicationService {
    
    private final CustomerRegistrationService customerRegistrationService;
    private final Customers customers;
    
    @Transactional
    public UUID create(CustomerInput input) {
        Objects.requireNonNull(input);
        AddressData address = Objects.requireNonNull(input.getAddress());

        Customer customer = customerRegistrationService.register(
                new FullName(input.getFirstName(), input.getLastName()),
                new BirthDate(input.getBirthDate()),
                new Email(input.getEmail()),
                new Phone(input.getPhone()),
                new Document(input.getDocument()),
                input.getPromotionNotificationsAllowed(),
                Address.builder()
                        .zipCode(new ZipCode(address.getZipCode()))
                        .state(address.getState())
                        .city(address.getCity())
                        .neighborhood(address.getNeighborhood())
                        .street(address.getStreet())
                        .number(address.getNumber())
                        .complement(address.getComplement())
                        .build()
        );

        customers.add(customer);
        
        return customer.id().value();
    }
    
    @Transactional(readOnly = true)
    public CustomerOutput findById(UUID customerId) {
        Objects.requireNonNull(customerId);
        Customer customer = customers.ofId(new CustomerId(customerId)).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        
        return CustomerOutput.builder()
                .id(customer.id().value())
                .firstName(customer.fullName().firstName())
                .lastName(customer.fullName().lastName())
                .email(customer.email().value())
                .document(customer.document().value())
                .phone(customer.phone().value())
                .promotionNotificationsAllowed(customer.isPromotionNotificationsAllowed())
                .loyaltyPoints(customer.loyaltyPoints().value())
                .registeredAt(customer.registeredAt())
                .archivedAt(customer.archivedAt() != null ? customer.archivedAt() : null)
                .birthDate(customer.birthDate().value() != null ? customer.birthDate().value() : null)
                .address(AddressData.builder()
                        .street(customer.address().street())
                        .number(customer.address().number())
                        .complement(customer.address().complement())
                        .neighborhood(customer.address().neighborhood())
                        .city(customer.address().city())
                        .state(customer.address().state())
                        .zipCode(customer.address().zipCode().value())
                        .build())
                .build();
    }
}
