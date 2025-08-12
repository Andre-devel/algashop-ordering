package com.algaworks.algashop.ordering.domain.entity.factory;

import com.algaworks.algashop.ordering.domain.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;

import java.util.Objects;

public class ShoppingFactory {
    private ShoppingFactory() {}
    
    public static ShoppingCart startShopping(CustomerId customerId) {
        Objects.requireNonNull(customerId);

        return ShoppingCart.startShopping(customerId);
    }
}
