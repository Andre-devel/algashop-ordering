package com.algaworks.algashop.ordering.aplication.order.management;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderCanceledEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderPaidEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderReadyEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class OrderManagementApplicationServiceIT {
    
    @Autowired
    private OrderManagementApplicationService orderManagementApplicationService;
    
    @Autowired
    private Orders orders;
    
    @Autowired
    private Customers customers;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @BeforeEach
    public void setup() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }
    
    @Test
    void shouldCancelOrder() {
        Order order = OrderTestDataBuilder.anOrder().build(); 
        orders.add(order);
        
        orderManagementApplicationService.cancel(order.id().value().toLong());
        Order canceledOrder = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(canceledOrder.status()).isEqualTo(OrderStatus.CANCELED);

        Mockito.verify(orderEventListener)
                .listen(Mockito.any(OrderCanceledEvent.class));
    }
    
    @Test
    void givenOrderNotExists_whenCancelOrder_thenThrowException() {
        Long nonExistentOrderId = 9999L;

        Assertions.assertThatThrownBy(() -> 
            orderManagementApplicationService.cancel(nonExistentOrderId)
        ).isInstanceOf(OrderNotFoundException.class);
    }
    
    @Test
    void givenOrderIsAlreadyCanceled_whenCancelOrder_thenThrowException() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.CANCELED)
                .build();
        orders.add(order);

        Assertions.assertThatThrownBy(() -> 
            orderManagementApplicationService.cancel(order.id().value().toLong())
        ).isInstanceOf(OrderStatusCannotBeChangedException.class);
    }
    
    @Test
    void shouldMarkOrderAsPaid() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build(); 
        orders.add(order);
        
        orderManagementApplicationService.markAsPaid(order.id().value().toLong());
        Order paidOrder = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(paidOrder.status()).isEqualTo(OrderStatus.PAID);

        Mockito.verify(orderEventListener)
                .listen(Mockito.any(OrderPaidEvent.class));
    }
    
    @Test
    void givenOrderNotExists_whenMarkAsPaid_thenThrowException() {
        Long nonExistentOrderId = 9999L;

        Assertions.assertThatThrownBy(() -> 
            orderManagementApplicationService.markAsPaid(nonExistentOrderId)
        ).isInstanceOf(OrderNotFoundException.class);
    }
    
    @Test
    void givenOrderIsAlreadyPaid_whenMarkAsPaid_thenThrowException() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PAID)
                .build();
        orders.add(order);

        Assertions.assertThatThrownBy(() ->
                orderManagementApplicationService.markAsPaid(order.id().value().toLong())
        ).isInstanceOf(OrderStatusCannotBeChangedException.class);
    }
    
    @Test
    void givenOrderIsCanceled_whenMarkAsPaid_thenThrowException() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.CANCELED)
                .build();
        orders.add(order);

        Assertions.assertThatThrownBy(() ->
                orderManagementApplicationService.markAsPaid(order.id().value().toLong())
        ).isInstanceOf(OrderStatusCannotBeChangedException.class);
    }
    
    @Test
    void shouldMarkOrderAsReady() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build(); 
        orders.add(order);
        
        orderManagementApplicationService.markAsReady(order.id().value().toLong());
        Order readyOrder = orders.ofId(order.id()).orElseThrow();

        Assertions.assertThat(readyOrder.status()).isEqualTo(OrderStatus.READY);

        Mockito.verify(orderEventListener)
                .listen(Mockito.any(OrderReadyEvent.class));
    }
    
    @Test
    void givenOrderNotExists_whenMarkAsReady_thenThrowException() {
        Long nonExistentOrderId = 9999L;

        Assertions.assertThatThrownBy(() -> 
            orderManagementApplicationService.markAsReady(nonExistentOrderId)
        ).isInstanceOf(OrderNotFoundException.class);
    }
    
    @Test
    void givenOrderIsAlreadyReady_whenMarkAsReady_thenThrowException() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.READY)
                .build();
        orders.add(order);

        Assertions.assertThatThrownBy(() ->
                orderManagementApplicationService.markAsReady(order.id().value().toLong())
        ).isInstanceOf(OrderStatusCannotBeChangedException.class);
    }
    
    @Test
    void givenOrderIsPlaced_whenMarkAsReady_thenThrowException() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PLACED)
                .build();
        orders.add(order);

        Assertions.assertThatThrownBy(() ->
                orderManagementApplicationService.markAsReady(order.id().value().toLong())
        ).isInstanceOf(OrderStatusCannotBeChangedException.class);
    }
    
    @Test
    void givenOrderValid_whenCancel_thenEventShouldBeTriggered() {
        Order order = OrderTestDataBuilder.anOrder().build(); 
        orders.add(order);
        
        orderManagementApplicationService.cancel(order.id().value().toLong());

        Mockito.verify(orderEventListener)
                .listen(Mockito.any(OrderCanceledEvent.class));
    }
    
    @Test
    void givenOrderValid_whenMarkAsPaid_thenEventShouldBeTriggered() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build(); 
        orders.add(order);
        
        orderManagementApplicationService.markAsPaid(order.id().value().toLong());

        Mockito.verify(orderEventListener)
                .listen(Mockito.any(OrderPaidEvent.class));
    }
    
    @Test
    void givenOrderValid_whenMarkAsReady_thenEventShouldBeTriggered() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build(); 
        orders.add(order);
        
        orderManagementApplicationService.markAsReady(order.id().value().toLong());

        Mockito.verify(orderEventListener)
                .listen(Mockito.any(OrderReadyEvent.class));
    }
}