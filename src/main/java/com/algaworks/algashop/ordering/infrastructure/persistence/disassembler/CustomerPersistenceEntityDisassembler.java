package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.valueobject.BirthDate;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import static com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.AddressDisassembler.addressEmbeddableToAddress;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceEntityDisassembler {
    
    public Customer toDomainEntity(CustomerPersistenceEntity customerPersistenceEntity) {
        return Customer.existing()
                .id(new CustomerId(customerPersistenceEntity.getId()))
                .fullName(new FullName(customerPersistenceEntity.getFirstName(), customerPersistenceEntity.getLastName()))
                .birthDate(new BirthDate(customerPersistenceEntity.getBirthDate()))
                .email(new Email(customerPersistenceEntity.getEmail()))
                .phone(new Phone(customerPersistenceEntity.getPhone()))
                .document(new Document(customerPersistenceEntity.getDocument()))
                .promotionNotificationsAllowed(customerPersistenceEntity.isPromotionNotificationsAllowed())
                .archived(customerPersistenceEntity.isArchived())
                .registeredAt(customerPersistenceEntity.getRegisteredAt())
                .archivedAt(customerPersistenceEntity.getArchivedAt())
                .loyaltyPoints(new LoyaltyPoints(customerPersistenceEntity.getLoyaltyPoints()))
                .address(addressEmbeddableToAddress(customerPersistenceEntity.getAddress()))
                .build();
    }
}
