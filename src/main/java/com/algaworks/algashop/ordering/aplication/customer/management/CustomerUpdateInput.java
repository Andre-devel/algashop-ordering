package com.algaworks.algashop.ordering.aplication.customer.management;

import com.algaworks.algashop.ordering.aplication.commons.AddressData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerUpdateInput {
    private String firstName;
    private String lastName;
    private String phone;
    private Boolean promotionNotificationsAllowed;
    private AddressData address;
    
}
