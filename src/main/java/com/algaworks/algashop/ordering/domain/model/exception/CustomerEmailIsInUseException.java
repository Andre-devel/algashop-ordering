package com.algaworks.algashop.ordering.domain.model.exception;

public class CustomerEmailIsInUseException extends DomainException {
    
    public CustomerEmailIsInUseException(Throwable cause) {
        super(cause);
    }

    public CustomerEmailIsInUseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomerEmailIsInUseException(String message) {
        super(message);
    }
}
