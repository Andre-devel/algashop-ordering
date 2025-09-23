package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.entity.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CheckoutServiceTest {
    
    private final CheckoutService checkoutService = new CheckoutService();
    
    @Test
    void shouldCheckoutSuccessfully() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThat(shoppingCart.containsUnavailableItems()).isFalse();
        
        Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

        ShoppingCart shoppingCart2 = ShoppingCartTestDataBuilder.aShoppingCart().build();

        Assertions.assertThat(order.customerId()).isNotNull();
                Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
                Assertions.assertThat(order.billing()).isEqualTo(billing);
                Assertions.assertThat(order.shipping()).isEqualTo(shipping);
                Assertions.assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
        
        Assertions.assertThat(order.totalAmount()).isEqualTo(shoppingCart2.totalAmount().add(shipping.cost()));
        Assertions.assertThat(order.totalItems()).isEqualTo(shoppingCart2.totalItems());
        Assertions.assertThat(order.items().size()).isEqualTo(shoppingCart2.items().size());
        
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
    }
    
    @Test
    void shouldNotCheckoutWhenCartIsUnavailable() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .build();

        Product productUnavailable = ProductTestDataBuilder.aProductUnavailable().build();
        shoppingCart.refreshItem(productUnavailable);
        
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThatThrownBy(() -> {
            checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);
        }).isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);
        
        Assertions.assertThat(shoppingCart.isEmpty()).isFalse();
    }
    
    @Test
    void shouldNotCheckoutWhenCartIsEmpty() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .withItems(false)
                .build();
        
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThatThrownBy(() -> {
            checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);
        }).isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);
        
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
    }

}