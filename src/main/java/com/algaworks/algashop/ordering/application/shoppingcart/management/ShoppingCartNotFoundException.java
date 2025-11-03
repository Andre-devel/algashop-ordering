package com.algaworks.algashop.ordering.application.shoppingcart.management;

import com.algaworks.algashop.ordering.domain.model.DomainEntityNotFoundException;

public class ShoppingCartNotFoundException extends DomainEntityNotFoundException {
    public ShoppingCartNotFoundException(String message) {
        super(message);
    }
}
