package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainException;

public class CustomerAlreadyHasShoppingCartException extends DomainException {
    public CustomerAlreadyHasShoppingCartException(Throwable cause) {
        super(cause);
    }

    public CustomerAlreadyHasShoppingCartException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerAlreadyHasShoppingCartException(String message) {
        super(message);
    }
}
