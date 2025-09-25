package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


class BuyNowServiceTest {
    private final BuyNowService buyNowService = new BuyNowService();
    
    @Test
    void shouldBuyNowSuccessfully() {
        CustomerId customerId = new CustomerId();
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customerId, billing, shipping, quantity, paymentMethod);
        
        Assertions.assertThat(order.totalItems()).isEqualTo(quantity);
        Assertions.assertThat(order.totalAmount()).isEqualTo(product.price().multiply(quantity).add(shipping.cost()));
        Assertions.assertThat(order.customerId()).isNotNull();
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping);
        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
    }
    
    @Test
    void shouldNotBuyNowWhenProductIsOutOfStock() {
        CustomerId customerId = new CustomerId();
        Product product = ProductTestDataBuilder.aProductUnavailable().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThatThrownBy(() -> {
            buyNowService.buyNow(product, customerId, billing, shipping, quantity, paymentMethod);
        }).isInstanceOf(ProductOutOfStockException.class);
    }
    
    @Test
    void shouldNotBuyNowWhenQuantityIsZero() {
        CustomerId customerId = new CustomerId();
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(0);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThatThrownBy(() -> {
            buyNowService.buyNow(product, customerId, billing, shipping, quantity, paymentMethod);
        }).isInstanceOf(IllegalArgumentException.class);
    }
}