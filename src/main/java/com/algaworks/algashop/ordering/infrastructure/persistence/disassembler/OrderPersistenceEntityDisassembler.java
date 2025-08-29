package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderItem;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.Address;
import com.algaworks.algashop.ordering.domain.model.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.model.valueobject.Document;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.Recipient;
import com.algaworks.algashop.ordering.domain.model.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.model.valueobject.ZipCode;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import static com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.AddressDisassembler.addressEmbeddableToAddress;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.embeddable.AddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.embeddable.BillingEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.embeddable.RecipientEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.embeddable.ShippingEmbeddable;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrderPersistenceEntityDisassembler {
    
    public Order toDomainEntity(OrderPersistenceEntity persistenceEntity) {
        return Order.existing()
                .id(new OrderId(persistenceEntity.getId()))
                .customerId(new CustomerId(persistenceEntity.getCustomerId()))
                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(persistenceEntity.getTotalItems()))
                .status(OrderStatus.valueOf(persistenceEntity.getStatus()))
                .paymentMethod(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
                .placedAt(persistenceEntity.getPlaceAt())
                .paidAt(persistenceEntity.getPaidAt())
                .cancelAt(persistenceEntity.getCancelAt())
                .readyAt(persistenceEntity.getReadyAt())
                .items(new HashSet<>())
                .version(persistenceEntity.getVersion())
                .shipping(shippingEmbeddableToShipping(persistenceEntity.getShipping()))
                .billing(billingEmbeddableToBilling(persistenceEntity.getBilling()))
                .items(orderItemPersistenceEntityToOrderItem(persistenceEntity.getItems()))
                .build();
    }
    
    public Set<OrderItem> orderItemPersistenceEntityToOrderItem(Set<OrderItemPersistenceEntity> orderItemsPersistenceEntity) {
        if (orderItemsPersistenceEntity == null || orderItemsPersistenceEntity.isEmpty()) {
            return new HashSet<>();
        }
        return orderItemsPersistenceEntity.stream().map(this::persistenceItemToItem).collect(Collectors.toSet());
    }
    
    private OrderItem persistenceItemToItem(OrderItemPersistenceEntity persistenceItem) {
        return OrderItem.Existing()
                .id(new OrderItemId(persistenceItem.getId()))
                .orderId(new OrderId(persistenceItem.getOrderId()))
                .productId(new ProductId(persistenceItem.getProductId()))
                .productName(new ProductName(persistenceItem.getProductName()))
                .price(new Money(persistenceItem.getPrice()))
                .quantity(new Quantity(persistenceItem.getQuantity()))
                .totalAmount(new Money(persistenceItem.getTotalAmount()))
                .build();
    }
    
    private Billing billingEmbeddableToBilling(BillingEmbeddable billingEmbeddable) {
        if (billingEmbeddable == null) {
            return null;
        }
        
        return Billing.builder()
                .fullName(new FullName(billingEmbeddable.getFirstName(), billingEmbeddable.getLastName()))
                .document(new Document(billingEmbeddable.getDocument()))
                .phone(new Phone(billingEmbeddable.getPhone()))
                .address(addressEmbeddableToAddress(billingEmbeddable.getAddress()))
                .email(new Email(billingEmbeddable.getEmail()))
                .build();
    }
    
    private Shipping shippingEmbeddableToShipping(ShippingEmbeddable shippingEmbeddable) {
        if (shippingEmbeddable == null) {
            return null;
        }
        
        return Shipping.builder()
                .cost(new Money(shippingEmbeddable.getCost()))
                .expectedDate(shippingEmbeddable.getExpectedDate())
                .recipient(recipientEmbeddableToRecipient(shippingEmbeddable.getRecipient()))
                .address(addressEmbeddableToAddress(shippingEmbeddable.getAddress()))
                .build();
    }
    
    private Recipient recipientEmbeddableToRecipient(RecipientEmbeddable recipientEmbeddable) {
        if (recipientEmbeddable == null) {
            return null;
        }
        
        return Recipient.builder()
                .fullName(new FullName(recipientEmbeddable.getFirstName(), recipientEmbeddable.getLastName()))
                .document(new Document(recipientEmbeddable.getDocument()))
                .phone(new Phone(recipientEmbeddable.getPhone()))
                .build();
    }
    
    
}
