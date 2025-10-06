package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.aplication.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.algaworks.algashop.ordering.aplication.customer.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderReadyEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;
import java.util.UUID;

@SpringBootTest
class CustomerEventListerIT {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @MockitoSpyBean
    private CustomerEventLister customerEventLister;

    @MockitoBean
    private CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @MockitoSpyBean
    private CustomerNotificationApplicationService customerNotificationApplicationService;
    
    @Test
    public void shouldListenerOrderReadyEvent() {
        eventPublisher.publishEvent(
                new OrderReadyEvent(
                        new OrderId(),
                        new CustomerId(),
                        OffsetDateTime.now()
                )
        );
        
        Mockito.verify(customerEventLister)
                .listen(Mockito.any(OrderReadyEvent.class));
        
        Mockito.verify(customerLoyaltyPointsApplicationService)
                .addLoyaltyPoints(Mockito.any(UUID.class), Mockito.anyString());
    }
    
    @Test
    public void shouldListenerCustomerRegisteredEvent() {
        eventPublisher.publishEvent(
                new CustomerRegisteredEvent(
                        new CustomerId(),
                        OffsetDateTime.now(),
                        new FullName("First", "Last"),
                        new Email("teste@email.com")
                )
        );

        Mockito.verify(customerEventLister)
                .listen(Mockito.any(CustomerRegisteredEvent.class));

        Mockito.verify(customerNotificationApplicationService)
                .notifyNewRegistration(Mockito.any(CustomerNotificationApplicationService.NotifyNewRegistrationInput.class));
    }
}