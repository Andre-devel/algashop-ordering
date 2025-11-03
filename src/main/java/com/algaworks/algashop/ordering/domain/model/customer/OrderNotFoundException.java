package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainEntityNotFoundException;

public class OrderNotFoundException extends DomainEntityNotFoundException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
