package com.algaworks.algashop.ordering.application.shoppingcart.management;

import com.algaworks.algashop.ordering.domain.model.DomainException;

public class ShoppingCartNotFoundException extends DomainException {
    public ShoppingCartNotFoundException(Throwable cause) {
        super(cause);
    }

    public ShoppingCartNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShoppingCartNotFoundException(String message) {
        super(message);
    }
}
