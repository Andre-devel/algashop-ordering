package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.BillingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.ShippingInfo;
import com.algaworks.algashop.ordering.domain.valueobject.ZipCode;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;

import java.time.LocalDate;

public class OrderTestDataBuilder {
    
    private CustomerId customerId = new CustomerId();
    
    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;
    
    private Money shippingCost = new Money("10");
    private LocalDate expectedDeliveryDate = LocalDate.now().plusWeeks(1);
    
    private ShippingInfo shippingInfo = aShippingInfo();

    private BillingInfo billingInfo = aBillingInfo();
    
    private boolean withItems = true;
    
    private OrderStatus status = OrderStatus.DRAFT;
    
    private OrderTestDataBuilder() {
    }
    
    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }
    
    public Order build() {
        Order order = Order.draft(customerId);
        order.changeShipping(shippingInfo, shippingCost, expectedDeliveryDate);
        order.changeBilling(billingInfo);
        order.changePaymentMethod(paymentMethod);
        
        if (withItems) {
            order.addItem(ProductTestDataBuilder.aProduct().build(),new Quantity(2));
            order.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(),new Quantity(1));
        }
        
        switch (this.status) {
            case DRAFT -> {}
            case PLACED -> order.place();
            case PAID -> {
                order.place();
                order.markAsPaid();
            }
            case READY -> {}
            case CANCELED -> {}
        }
        
        return order;
    }

    public static ShippingInfo aShippingInfo() {
        return ShippingInfo.builder()
                .address(anAddress())
                .fullName(new FullName("John", "Doe"))
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911")).build();
    }

    public static BillingInfo aBillingInfo() {
        return BillingInfo.builder()
                .address(anAddress())
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .fullName(new FullName("John", "Doe")).build();
    }

    public static Address anAddress() {
        return Address.builder()
                .street("Balanced street")
                .number("1234")
                .neighborhood("North Ville")
                .city("Montfort")
                .state("South Carolina")
                .zipCode(new ZipCode("79911")).build();
    }

    public OrderTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTestDataBuilder paymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderTestDataBuilder shippingCost(Money shippingCost) {
        this.shippingCost = shippingCost;
        return this;
    }

    public OrderTestDataBuilder expectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
        return this;
    }

    public OrderTestDataBuilder shippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
        return this;
    }

    public OrderTestDataBuilder billingInfo(BillingInfo billingInfo) {
        this.billingInfo = billingInfo;
        return this;
    }

    public OrderTestDataBuilder withItems(boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public OrderTestDataBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }
}
