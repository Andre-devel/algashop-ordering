package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.embeddable.AddressEmbeddable;

import java.util.Objects;

public class AddressAssembler {
    public static AddressEmbeddable addressToAddressEmbeddable(Address address) {
        Objects.requireNonNull(address);

        return AddressEmbeddable.builder()
                .street(address.street())
                .number(address.number())
                .complement(address.complement())
                .neighborhood(address.neighborhood())
                .city(address.city())
                .state(address.state())
                .zipCode(address.zipCode().value())
                .build();
    }
}
