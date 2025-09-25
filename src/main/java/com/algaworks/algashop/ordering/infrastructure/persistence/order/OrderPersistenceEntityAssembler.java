package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.domain.model.order.Billing;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderItem;
import com.algaworks.algashop.ordering.domain.model.order.Recipient;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import static com.algaworks.algashop.ordering.infrastructure.persistence.commons.AddressAssembler.addressToAddressEmbeddable;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderPersistenceEntityAssembler {
    
    private final CustomerPersistenceEntityRepository customerRepository;       
    
    public OrderPersistenceEntity fromDomain(Order order) {
        return merge(new OrderPersistenceEntity(), order);
    }
    
    public OrderPersistenceEntity merge(OrderPersistenceEntity orderPersistenceEntity, Order order) {
        orderPersistenceEntity.setId(order.id().value().toLong());
        orderPersistenceEntity.setTotalAmount(order.totalAmount().value());
        orderPersistenceEntity.setTotalItems(order.totalItems().value());
        orderPersistenceEntity.setStatus(order.status().name());
        orderPersistenceEntity.setPaymentMethod(order.paymentMethod().name());
        orderPersistenceEntity.setPlacedAt(order.placedAt());
        orderPersistenceEntity.setPaidAt(order.paidAt());
        orderPersistenceEntity.setCancelAt(order.cancelAt());
        orderPersistenceEntity.setReadyAt(order.readyAt());
        orderPersistenceEntity.setVersion(order.version());
        orderPersistenceEntity.setBilling(billingToBillingEmbeddable(order.billing()));
        orderPersistenceEntity.setShipping(shippingToShippingEmbeddable(order.shipping()));
        
        Set<OrderItemPersistenceEntity> mergeItems = mergeItems(order, orderPersistenceEntity);
        orderPersistenceEntity.replaceItems(mergeItems);

        CustomerPersistenceEntity CustomerReference = customerRepository.getReferenceById(order.customerId().value());
        orderPersistenceEntity.setCustomer(CustomerReference);
        
        return orderPersistenceEntity;
    }

    private Set<OrderItemPersistenceEntity> mergeItems(Order order, OrderPersistenceEntity orderPersistenceEntity) {
        Set<OrderItem> newOrUpdatedItems = order.items();
        
        if (newOrUpdatedItems == null || newOrUpdatedItems.isEmpty()) {
            return new HashSet<>();
        }

        Set<OrderItemPersistenceEntity> existingItems = orderPersistenceEntity.getItems();
        
        if (existingItems == null || existingItems.isEmpty()) {
            return newOrUpdatedItems.stream().map(this::fromDomain).collect(Collectors.toSet());
        }

        Map<Long, OrderItemPersistenceEntity> existingItemMap = existingItems.stream()
                .collect(Collectors.toMap(OrderItemPersistenceEntity::getId, item -> item));
        
        return newOrUpdatedItems.stream()
                .map(orderItem -> {
                    OrderItemPersistenceEntity itemsPersistence = existingItemMap
                            .getOrDefault(orderItem.id().value().toLong(), new OrderItemPersistenceEntity());
                    return merge(itemsPersistence, orderItem);
                }).collect(Collectors.toSet());
    }

    public OrderItemPersistenceEntity fromDomain(OrderItem orderItem) {
        return merge(new OrderItemPersistenceEntity(), orderItem);
    }

    private OrderItemPersistenceEntity merge(OrderItemPersistenceEntity orderItemPersistenceEntity, OrderItem orderItem) {
        orderItemPersistenceEntity.setId(orderItem.id().value().toLong());
        orderItemPersistenceEntity.setProductId(orderItem.productId().value());
        orderItemPersistenceEntity.setProductName(orderItem.productName().value());
        orderItemPersistenceEntity.setPrice(orderItem.price().value());
        orderItemPersistenceEntity.setQuantity(orderItem.quantity().value());
        orderItemPersistenceEntity.setTotalAmount(orderItem.totalAmount().value());
        
        return orderItemPersistenceEntity;
    }

    private BillingEmbeddable billingToBillingEmbeddable(Billing billing) {
        Objects.requireNonNull(billing);
        
        return BillingEmbeddable.builder()
                .firstName(billing.fullName().firstName())
                .lastName(billing.fullName().lastName())
                .document(billing.document().value())
                .phone(billing.phone().value())
                .address(addressToAddressEmbeddable(billing.address()))
                .email(billing.email().value())
                .build();

    }
    
    private ShippingEmbeddable shippingToShippingEmbeddable(Shipping shipping) {
        Objects.requireNonNull(shipping);
        
        return ShippingEmbeddable.builder()
                .cost(shipping.cost().value())
                .expectedDate(shipping.expectedDate())
                .address(addressToAddressEmbeddable(shipping.address()))
                .recipient(recipientToRecipientEmbeddable(shipping.recipient())).build();
                
    }
    
  
    
    private RecipientEmbeddable recipientToRecipientEmbeddable(Recipient recipient) {
        Objects.requireNonNull(recipient);
        
        return RecipientEmbeddable.builder()
                .firstName(recipient.fullName().firstName())
                .lastName(recipient.fullName().firstName())
                .document(recipient.document().value())
                .phone(recipient.phone().value())
                .build();
    }
}
