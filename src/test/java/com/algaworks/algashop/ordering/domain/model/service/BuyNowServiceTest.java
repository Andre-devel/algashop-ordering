package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.entity.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Product;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
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