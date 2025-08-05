package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.valueobject.Address;
import com.algaworks.algashop.ordering.domain.valueobject.Billing;
import com.algaworks.algashop.ordering.domain.valueobject.Document;
import com.algaworks.algashop.ordering.domain.valueobject.Email;
import com.algaworks.algashop.ordering.domain.valueobject.FullName;
import com.algaworks.algashop.ordering.domain.valueobject.Money;
import com.algaworks.algashop.ordering.domain.valueobject.Phone;
import com.algaworks.algashop.ordering.domain.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.valueobject.Recipient;
import com.algaworks.algashop.ordering.domain.valueobject.Shipping;
import com.algaworks.algashop.ordering.domain.valueobject.ZipCode;
import com.algaworks.algashop.ordering.domain.valueobject.id.CustomerId;

import java.time.LocalDate;

public class OrderTestDataBuilder {
    
    private CustomerId customerId = new CustomerId();
    
    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;
    
    private Shipping shipping = aShipping();

    private Billing billing = aBilling();
    
    private boolean withItems = true;
    
    private OrderStatus status = OrderStatus.DRAFT;
    
    private OrderTestDataBuilder() {
    }
    
    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }
    
    public Order build() {
        Order order = Order.draft(customerId);
        order.changeShipping(shipping);
        order.changeBilling(billing);
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
            case READY -> {
                order.place();
                order.markAsReady();
                order.markAsReady();
            }
            case CANCELED -> {}
        }
        
        return order;
    }

    public static Shipping aShipping() {
        return Shipping.builder()
                .cost(new Money("10"))
                .expectedDate(LocalDate.now())
                .address(anAddress())
                .recipient(Recipient.builder()
                        .fullName(new FullName("John", "Doe"))
                        .document(new Document("225-09-1992"))
                        .phone(new Phone("123-111-9911")).build())
                .build();
    }

    public static Shipping aShippingAlt() {
        return Shipping.builder()
                .cost(new Money("20.00"))
                .expectedDate(LocalDate.now().plusWeeks(2))
                .address(anAddressAlt())
                .recipient(Recipient.builder()
                        .fullName(new FullName("Mary", "Doe"))
                        .document(new Document("135-02-1672"))
                        .phone(new Phone("175-457-9212")).build())
                .build();
    }

    public static Billing aBilling() {
        return Billing.builder()
                .address(anAddress())
                .document(new Document("225-09-1992"))
                .phone(new Phone("123-111-9911"))
                .email(new Email("John@hotmail.com"))
                .fullName(new FullName("John", "Doe")).build();
    }

    public static Billing aBillingAlt() {
        return Billing.builder()
                .address(anAddress())
                .document(new Document("563-02-1945"))
                .phone(new Phone("267-567-1235"))
                .email(new Email("Johnchange@hotmail.com"))
                .fullName(new FullName("change", "fox")).build();
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

    public static Address anAddressAlt() {
        return Address.builder()
                .street("Sansome Steet")
                .number("875")
                .neighborhood("Sansome")
                .city("San francisco")
                .state("California")
                .zipCode(new ZipCode("08040")).build();
    }

    public OrderTestDataBuilder customerId(CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTestDataBuilder paymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderTestDataBuilder shipping(Shipping shipping) {
        this.shipping = shipping;
        return this;
    }

    public OrderTestDataBuilder billing(Billing billing) {
        this.billing = billing;
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
