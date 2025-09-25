package com.algaworks.algashop.ordering.infrastructure.persistence.commons;

import com.algaworks.algashop.ordering.domain.model.commons.Address;

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
