package com.algaworks.algashop.ordering.aplication.customer.query;

import java.util.UUID;

public interface CustomerQueryService {
    CustomerOutput findById(UUID customerId);
}
