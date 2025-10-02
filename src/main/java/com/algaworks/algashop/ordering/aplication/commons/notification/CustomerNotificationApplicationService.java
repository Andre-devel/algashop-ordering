package com.algaworks.algashop.ordering.aplication.commons.notification;

import java.util.UUID;

public interface CustomerNotificationApplicationService {
    void notifyNewRegistration(UUID customerId);
}
