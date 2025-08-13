package com.algaworks.algashop.ordering.domain.model.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderCancelTest {

    @Test
    public void givenPlaceOrder_whenDraft_shouldChangeToCancel() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).build();
        order.cancel();
        Assertions.assertThat(order.status().equals(OrderStatus.CANCELED)).isTrue();
        Assertions.assertThat(order.cancelAt()).isNotNull();
    }

    @Test
    public void givenPlaceOrder_whenPlace_shouldChangeToCancel() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        order.cancel();
        Assertions.assertThat(order.status().equals(OrderStatus.CANCELED)).isTrue();
        Assertions.assertThat(order.cancelAt()).isNotNull();
    }
    
    @Test
    public void givenPlaceOrder_whenPaid_shouldChangeToCancel() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        order.cancel();
        Assertions.assertThat(order.status().equals(OrderStatus.CANCELED)).isTrue();
        Assertions.assertThat(order.cancelAt()).isNotNull();
    }

    @Test
    public void givenPlaceOrder_whenReady_shouldChangeToCancel() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();
        order.cancel();
        Assertions.assertThat(order.status().equals(OrderStatus.CANCELED)).isTrue();
        Assertions.assertThat(order.cancelAt()).isNotNull();
    }

}
