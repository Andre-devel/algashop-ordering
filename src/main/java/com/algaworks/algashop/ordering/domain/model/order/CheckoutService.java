package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;

@DomainService
public class CheckoutService {
    public Order checkout(ShoppingCart shoppingCart,
                          Billing billing,
                          Shipping shipping,
                          PaymentMethod paymentMethod) {
        
        if (shoppingCart.isEmpty() || shoppingCart.containsUnavailableItems()) {
            throw new ShoppingCartCantProceedToCheckoutException("Shopping cart is empty or contains unavailable items.");
        }

        Order order = Order.draft(shoppingCart.customerId());
        order.changeBilling(billing);
        order.changeShipping(shipping);
        order.changePaymentMethod(paymentMethod);
        
        shoppingCart.items().forEach(shoppingCartItem -> {
            Product product = new Product(
                    shoppingCartItem.productId(),
                    shoppingCartItem.name(),
                    shoppingCartItem.price(),
                    shoppingCartItem.isAvailable()
            );
            
            order.addItem(product, shoppingCartItem.quantity());
        });
        
        order.place();
        shoppingCart.empty();
        
        return order;
    }
}
