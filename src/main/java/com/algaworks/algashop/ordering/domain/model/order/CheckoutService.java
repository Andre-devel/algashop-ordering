package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@DomainService
public class CheckoutService {
    
    private final CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification;
    
    public Order checkout(
            Customer customer,
            ShoppingCart shoppingCart,
            Billing billing,
            Shipping shipping,
            PaymentMethod paymentMethod
    ) {
        
        if (shoppingCart.isEmpty() || shoppingCart.containsUnavailableItems()) {
            throw new ShoppingCartCantProceedToCheckoutException("Shopping cart is empty or contains unavailable items.");
        }

        Order order = Order.draft(shoppingCart.customerId());
        order.changeBilling(billing);
        
        if (haveFreeShipping(customer)) {
            Shipping freeShipping = shipping.toBuilder().cost(Money.ZERO).build();
            order.changeShipping(freeShipping);
        } else {
            order.changeShipping(shipping);
        }
        
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
    
    private boolean haveFreeShipping(Customer customer) {
        return customerHaveFreeShippingSpecification.isSatisfiedBy(customer);
    }
}
