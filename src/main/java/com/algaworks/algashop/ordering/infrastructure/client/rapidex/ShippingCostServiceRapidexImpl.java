package com.algaworks.algashop.ordering.infrastructure.client.rapidex;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "algashop.integration.shipping.provider", havingValue = "RAPIDEX")
public class ShippingCostServiceRapidexImpl implements ShippingCostService {
    
    private final RapiDexAPIClient rapiDexAPIClient;
    
    @Override
    public CalculationResult calculate(CalculationRequest calculationRequest) {
        DeliveryCostResponse response = rapiDexAPIClient.calculate(new DeliveryCostRequest(
                calculationRequest.origin().value(),
                calculationRequest.destination().value()
        ));

        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(response.getEstimatedDaysToDeliver());

        return CalculationResult.builder()
                .cost(new Money(response.getDeliveryCost()))
                .expectedDate(expectedDeliveryDate)
                .build();
    }
}
