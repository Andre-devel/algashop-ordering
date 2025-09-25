package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainException;

public class OrderNotBelongToCustomerException extends DomainException {
    public OrderNotBelongToCustomerException() {
        super("The order does not belong to the customer.");
    }

    public OrderNotBelongToCustomerException(Throwable cause) {
        super(cause);
    }

    public OrderNotBelongToCustomerException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotBelongToCustomerException(String message) {
        super(message);
    }
}
