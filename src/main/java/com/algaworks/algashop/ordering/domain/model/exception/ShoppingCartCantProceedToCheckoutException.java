package com.algaworks.algashop.ordering.domain.model.exception;

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
