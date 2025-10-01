package com.algaworks.algashop.ordering.aplication.checkout;

import com.algaworks.algashop.ordering.aplication.shoppingcart.management.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import com.algaworks.algashop.ordering.domain.model.order.Billing;
import com.algaworks.algashop.ordering.domain.model.order.CheckoutService;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.order.Shipping;
import com.algaworks.algashop.ordering.domain.model.order.shipping.OriginAddressService;
import com.algaworks.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CheckoutApplicationService {
    
    private final ShoppingCarts shoppingCarts;
    private final ShippingCostService shippingCostService;
    private final OriginAddressService originAddressService;
    
    private final ShippingInputDisassembler shippingInputDisassembler;
    private final BillingInputDisassembler billingInputDisassembler;
    
    private final Orders orders;
    
    private final CheckoutService checkoutService;

    public String checkout(CheckoutInput input) {
        Objects.requireNonNull(input);

        PaymentMethod paymentMethod = PaymentMethod.valueOf(input.getPaymentMethod());
        ShoppingCart shoppingCart = shoppingCarts.ofId(new ShoppingCartId(input.getShoppingCartId()))
                .orElseThrow(() -> new ShoppingCartNotFoundException("Shopping cart not found: " + input.getShoppingCartId()));

        ZipCode origin = originAddressService.originAddress().zipCode();
        ZipCode destination = new ZipCode(input.getShipping().getAddress().getZipCode());
        
        ShippingCostService.CalculationRequest calculationRequest = ShippingCostService.CalculationRequest.builder()
                .origin(origin)
                .destination(destination)
                .build();

        ShippingCostService.CalculationResult resultCalc = shippingCostService.calculate(calculationRequest);

        Shipping shipping = shippingInputDisassembler.toDomainModel(input.getShipping(), resultCalc);
        Billing billing = billingInputDisassembler.toDomainModel(input.getBilling());

        Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);
        
        orders.add(order);
        shoppingCarts.add(shoppingCart);
        
        return order.id().toString();
    }
}
