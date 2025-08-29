package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;


import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntityTestDataBuilder;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

public class CustomerPersistenceEntityDisassemblerTest {

    private final CustomerPersistenceEntityDisassembler disassembler = new CustomerPersistenceEntityDisassembler();

    @Test
    void shouldConvertToEntity() {
        CustomerPersistenceEntity customerPersistenceEntity = CustomerPersistenceEntityTestDataBuilder.existingCustomer().build();

        Customer customer = disassembler.toDomainEntity(customerPersistenceEntity);
        Address address = customer.address();

        assertThat(customerPersistenceEntity).satisfies(
                entity -> {
                    Assertions.assertThat(entity.getId()).isEqualTo(customer.id().value());
                    Assertions.assertThat(entity.getFirstName()).isEqualTo(customer.fullName().firstName());
                    Assertions.assertThat(entity.getLastName()).isEqualTo(customer.fullName().lastName());
                    Assertions.assertThat(entity.getBirthDate()).isEqualTo(customer.birthDate().value());
                    Assertions.assertThat(entity.getEmail()).isEqualTo(customer.email().value());
                    Assertions.assertThat(entity.getPhone()).isEqualTo(customer.phone().value());
                    Assertions.assertThat(entity.getDocument()).isEqualTo(customer.document().value());
                    Assertions.assertThat(entity.isPromotionNotificationsAllowed()).isEqualTo(customer.isPromotionNotificationsAllowed());
                    Assertions.assertThat(entity.isArchived()).isEqualTo(customer.isArchived());
                    Assertions.assertThat(entity.getRegisteredAt()).isEqualTo(customer.registeredAt());
                    Assertions.assertThat(entity.getArchivedAt()).isEqualTo(customer.archivedAt());
                    Assertions.assertThat(entity.getLoyaltyPoints()).isEqualTo(customer.loyaltyPoints().value());

                    Assertions.assertThat(entity.getAddress().getStreet()).isEqualTo(address.street());
                    Assertions.assertThat(entity.getAddress().getNumber()).isEqualTo(address.number());
                    Assertions.assertThat(entity.getAddress().getComplement()).isEqualTo(address.complement());
                    Assertions.assertThat(entity.getAddress().getNeighborhood()).isEqualTo(address.neighborhood());
                    Assertions.assertThat(entity.getAddress().getCity()).isEqualTo(address.city());
                    Assertions.assertThat(entity.getAddress().getState()).isEqualTo(address.state());
                    Assertions.assertThat(entity.getAddress().getZipCode()).isEqualTo(address.zipCode().value());
                }
        );
    }
}
