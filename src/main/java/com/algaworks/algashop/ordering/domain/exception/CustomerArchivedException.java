package com.algaworks.algashop.ordering.domain.exception;


public class CustomerArchivedException extends DomainException {

    public CustomerArchivedException() {
        super(ErrorMessage.ERROR_CUSTOMER_ARCHIVED);
    }

    public CustomerArchivedException(Throwable cause) {
        super(ErrorMessage.ERROR_CUSTOMER_ARCHIVED, cause);
    }
}
