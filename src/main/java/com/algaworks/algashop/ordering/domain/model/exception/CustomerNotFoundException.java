package com.algaworks.algashop.ordering.domain.model.exception;

public class CustomerNotFoundException extends DomainException {
    
    public CustomerNotFoundException(Throwable cause) {
        super(cause);
    }

    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerNotFoundException(String message) {
        super(message);
    }
}
