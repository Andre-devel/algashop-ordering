package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainException;

public class OrderNotBelongsToCustomerException extends DomainException {
    public OrderNotBelongsToCustomerException() {
        super("The order does not belong to the customer.");
    }

    public OrderNotBelongsToCustomerException(Throwable cause) {
        super(cause);
    }

    public OrderNotBelongsToCustomerException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotBelongsToCustomerException(String message) {
        super(message);
    }
}
