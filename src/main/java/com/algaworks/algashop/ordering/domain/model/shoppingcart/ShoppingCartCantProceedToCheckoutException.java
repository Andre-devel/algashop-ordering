package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.DomainException;

public class ShoppingCartCantProceedToCheckoutException extends DomainException {
    
    public ShoppingCartCantProceedToCheckoutException(Throwable cause) {
        super(cause);
    }

    public ShoppingCartCantProceedToCheckoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShoppingCartCantProceedToCheckoutException(String message) {
        super(message);
    }
}
