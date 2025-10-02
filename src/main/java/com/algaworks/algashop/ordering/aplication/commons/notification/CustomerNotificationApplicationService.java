package com.algaworks.algashop.ordering.aplication.commons.notification;

import java.util.UUID;

public interface CustomerNotificationApplicationService {
    void notifyNewRegistration(NotifyNewRegistrationInput customerId);
    
    record NotifyNewRegistrationInput(UUID customerId, String fistName, String email) { }
}
