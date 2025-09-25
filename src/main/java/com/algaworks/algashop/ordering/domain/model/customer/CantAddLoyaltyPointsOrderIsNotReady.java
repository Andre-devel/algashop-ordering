package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainException;

public class CantAddLoyaltyPointsOrderIsNotReady extends DomainException {
    
    public CantAddLoyaltyPointsOrderIsNotReady() {
        super("Can't add loyalty points because the order is not ready");
    }
    
    public CantAddLoyaltyPointsOrderIsNotReady(Throwable cause) {
        super(cause);
    }

    public CantAddLoyaltyPointsOrderIsNotReady(String message, Throwable cause) {
        super(message, cause);
    }

    public CantAddLoyaltyPointsOrderIsNotReady(String message) {
        super(message);
    }
}
