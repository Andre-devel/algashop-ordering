package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.exception.OrderCannotBePlacedException;
import com.algaworks.algashop.ordering.domain.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.algaworks.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.algaworks.algashop.ordering.domain.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Product;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.domain.valueobject.id.OrderItemId;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Order {
    private OrderId id;
    private CustomerId customerId;
    
    private Money totalAmount;
    private Quantity totalItems;
    
    private OffsetDateTime placedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime cancelAt;
    private OffsetDateTime readyAt;
    
    private Billing billing;
    private Shipping shipping;
    
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    
    private Set<OrderItem> items;
    
    @Builder(builderClassName = "ExistingOrderBuilder", builderMethodName = "existing")
    public Order(
            OrderId id,
            CustomerId customerId,
            Money totalAmount,
            Quantity totalItems,
            OffsetDateTime placedAt,
            OffsetDateTime paidAt,
            OffsetDateTime cancelAt,
            OffsetDateTime readyAt,
            Billing billing,
            Shipping shipping,
            OrderStatus status,
            PaymentMethod paymentMethod,
            Set<OrderItem> items
    ) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setPlacedAt(placedAt);
        this.setPaidAt(paidAt);
        this.setCancelAt(cancelAt);
        this.setReadyAt(readyAt);
        this.setBilling(billing);
        this.setShipping(shipping);
        this.setStatus(status);
        this.setPaymentMethod(paymentMethod);
        this.setItems(items);
    }
    
    @Builder(builderClassName = "ExistingOrderItemBuilder", builderMethodName = "Existing")
    public static Order draft(CustomerId customerId) {
        return new Order(
                new OrderId(),
                customerId,
                Money.ZERO,
                Quantity.ZERO,
                null,
                null,
                null,
                null,
                null,
                null,
                OrderStatus.DRAFT,
                null,
                new HashSet<>()
        );
    }
    
    public void addItem(Product product, Quantity quantity) {
        verifyIfChangeable();
        
        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);
        
        product.checkOutOfStock();
        
        OrderItem orderItem = OrderItem.brandNew()
                .orderId(this.id)
                .quantity(quantity)
                .product(product)
                .build();

        this.items.add(orderItem);
        
        this.recalculateTotals();
    }
    
    public void place() {
        this.verifyIfCanChangeToPlace();
        
        this.changeStatus(OrderStatus.PLACED);
        this.setPlacedAt(OffsetDateTime.now());
    }

    public void markAsPaid() {
        this.changeStatus(OrderStatus.PAID);
        this.setPaidAt(OffsetDateTime.now());
    }
    
    public void markAsReady() {
        this.changeStatus(OrderStatus.READY);
        this.setReadyAt(OffsetDateTime.now());
    }
    
    public void changePaymentMethod(PaymentMethod paymentMethod) {
        verifyIfChangeable();
        
        Objects.requireNonNull(paymentMethod);
        this.setPaymentMethod(paymentMethod);
    }
    
    public void changeBilling(Billing billing) {
        verifyIfChangeable();
        
        Objects.requireNonNull(billing);
        this.setBilling(billing);
    }
    
    public void changeShipping(Shipping newShipping) {
        verifyIfChangeable();
        
        Objects.requireNonNull(newShipping);
        
        if (newShipping.expectedDate().isBefore(LocalDate.now())) {
            throw new OrderInvalidShippingDeliveryDateException(this.id);
        }
        
        this.setShipping(newShipping);
    }
    
    public void changeItemQuantity(OrderItemId orderItemId, Quantity quantity) {
        verifyIfChangeable();
        
        Objects.requireNonNull(orderItemId);
        Objects.requireNonNull(quantity);
        
        OrderItem orderItem = this.findOrderItem(orderItemId);
        orderItem.changeQuantity(quantity);
        
        this.recalculateTotals();
    }
    
    public void removeItem(OrderItemId orderItemId) {
        verifyIfChangeable();
        Objects.requireNonNull(orderItemId);

        OrderItem removeItem = findOrderItem(orderItemId);
        this.items.remove(removeItem);
        
        recalculateTotals();
    }

    public boolean isDraft() {
        return OrderStatus.DRAFT.equals(this.status());
    }

    public boolean isPlaced() {
        return OrderStatus.PLACED.equals(this.status());
    }

    public boolean isPaid() {
        return OrderStatus.PAID.equals(this.status);
    }

    public OffsetDateTime cancelAt() {
        return cancelAt;
    }

    public OrderId id() {
        return id;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public OffsetDateTime placedAt() {
        return placedAt;
    }

    public OffsetDateTime paidAt() {
        return paidAt;
    }

    public OffsetDateTime readyAt() {
        return readyAt;
    }

    public Billing billing() {
        return billing;
    }

    public Shipping shipping() {
        return shipping;
    }

    public OrderStatus status() {
        return status;
    }

    public PaymentMethod paymentMethod() {
        return paymentMethod;
    }

    public Set<OrderItem> items() {
        return Collections.unmodifiableSet(this.items);
    }

    private void recalculateTotals() {
        BigDecimal totalItemsAmount = this.items().stream().map(item -> item.totalAmount().value()).reduce(BigDecimal.ZERO, BigDecimal::add);
        Integer totalQuantity = this.items.stream().map(item -> item.quantity().value()).reduce(0, Integer::sum);
        
        BigDecimal shippingCost;
        if (this.shipping == null) {
            shippingCost = BigDecimal.ZERO;
        } else {
            shippingCost = this.shipping.cost().value();
        }
        
        BigDecimal totalAmount = totalItemsAmount.add(shippingCost);
        
        this.setTotalAmount(new Money(totalAmount));
        this.setTotalItems(new Quantity(totalQuantity));
    }

    private void changeStatus(OrderStatus newStatus) {
        Objects.requireNonNull(newStatus);

        if (this.status.canNotChangeTo(newStatus)) {
            throw new OrderStatusCannotBeChangedException(this.id, this.status, newStatus);
        }

        this.setStatus(newStatus);
    }

    private void verifyIfCanChangeToPlace() {
        if (this.shipping() == null) {
            throw OrderCannotBePlacedException.noShippingInfo(this.id);
        }

        if (this.billing() == null) {
            throw OrderCannotBePlacedException.noBillingInfo(this.id);
        }

        if (this.paymentMethod() == null) {
            throw OrderCannotBePlacedException.noPaymentMethod(this.id);
        }
        
        if (this.items == null || this.items().isEmpty()) {
            throw OrderCannotBePlacedException.noItems(this.id);
        }
    }

    private OrderItem findOrderItem(OrderItemId orderItemId) {
        Objects.requireNonNull(orderItemId);
        
        return this.items.stream()
                .filter(item -> item.id().equals(orderItemId))
                .findFirst()
                .orElseThrow(() -> new OrderDoesNotContainOrderItemException(this.id, orderItemId));
        
    }
    
    private void verifyIfChangeable() {
        if (!this.isDraft()) {
            throw new OrderCannotBeEditedException(this.id(), this.status());
        }
    }

    private void setId(OrderId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    private void setCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setTotalItems(Quantity totalItems) {
        Objects.requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setPlacedAt(OffsetDateTime placedAt) {
        this.placedAt = placedAt;
    }

    private void setPaidAt(OffsetDateTime paidAt) {
        this.paidAt = paidAt;
    }

    private void setCancelAt(OffsetDateTime cancelAt) {
        this.cancelAt = cancelAt;
    }

    private void setReadyAt(OffsetDateTime readyAt) {
        this.readyAt = readyAt;
    }

    private void setBilling(Billing billing) {
        this.billing = billing;
    }

    private void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    private void setStatus(OrderStatus status) {
        Objects.requireNonNull(status);
        this.status = status;
    }

    private void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    private void setItems(Set<OrderItem> items) {
        Objects.requireNonNull(items);
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
