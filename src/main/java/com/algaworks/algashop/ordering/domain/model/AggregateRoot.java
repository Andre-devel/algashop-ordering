package com.algaworks.algashop.ordering.domain.model;

import com.algaworks.algashop.ordering.DomainEventSource;

public interface AggregateRoot<ID> extends DomainEventSource {
    ID id();
}
