package com.algaworks.algashop.ordering.domain.model.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderMarkAsReadyTest {
    
    @Test
    public void givenPlaceOrder_whenPaid_shouldChangeToReady() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        order.markAsReady();
        Assertions.assertThat(order.status().equals(OrderStatus.READY)).isTrue();
        Assertions.assertThat(order.readyAt()).isNotNull();
    }
}
