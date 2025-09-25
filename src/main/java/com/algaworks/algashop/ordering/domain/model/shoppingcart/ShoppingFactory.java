package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;

import java.util.Objects;

public class ShoppingFactory {
    private ShoppingFactory() {}
    
    public static ShoppingCart startShopping(CustomerId customerId) {
        Objects.requireNonNull(customerId);

        return ShoppingCart.startShopping(customerId);
    }
}
