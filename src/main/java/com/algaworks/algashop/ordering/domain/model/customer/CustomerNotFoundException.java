package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainEntityNotFoundException;

public class CustomerNotFoundException extends DomainEntityNotFoundException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
    
}
