package com.algaworks.algashop.ordering.infrastructure.listener.customer;

import com.algaworks.algashop.ordering.aplication.commons.notification.CustomerNotificationApplicationService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedEvent;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerEventLister {
    
    private final CustomerNotificationApplicationService customerNotificationApplicationService;
    
    @EventListener
    public void listen(CustomerRegisteredEvent event) {
        log.info("Customer registered listen 1: {}", event.customerId().value());
        customerNotificationApplicationService.notifyNewRegistration(event.customerId().value());
    }
    
    @EventListener
    public void listen(CustomerArchivedEvent event) {
        log.info("Customer archived listen1: {}", event.customerId().value());
    }
}
