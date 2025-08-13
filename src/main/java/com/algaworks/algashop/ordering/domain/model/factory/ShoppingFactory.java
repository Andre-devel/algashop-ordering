package com.algaworks.algashop.ordering.domain.model.factory;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import java.util.Objects;

public class ShoppingFactory {
    private ShoppingFactory() {}
    
    public static ShoppingCart startShopping(CustomerId customerId) {
        Objects.requireNonNull(customerId);

        return ShoppingCart.startShopping(customerId);
    }
}
