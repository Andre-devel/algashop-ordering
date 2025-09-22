package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerEmailIsInUseException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerRegistrationService {
    
    private final Customers customers;
    
    public Customer register(
            FullName fullName,
            BirthDate birthDate,
            Email email,
            Phone phone,
            Document document,
            boolean promotionNotificationsAllowed,
            Address address
    ) {
        Customer customer = Customer.brandNew()
                .fullName(fullName)
                .birthDate(birthDate)
                .email(email)
                .phone(phone)
                .document(document)
                .promotionNotificationsAllowed(promotionNotificationsAllowed)
                .address(address)
                .build();
        
        verifyUniqueDocument(customer.email(), customer.id());
        
        return customer;
    }
    
    public void changeEmail(Customer customer, Email newEmail) {
        verifyUniqueDocument(newEmail, customer.id());
        customer.changeEmail(newEmail);
    }

    private void verifyUniqueDocument(Email email, CustomerId id) {
        if (!customers.isEmailUnique(email, id)) {
            throw new CustomerEmailIsInUseException("Email already in use");
        }
    }

}
