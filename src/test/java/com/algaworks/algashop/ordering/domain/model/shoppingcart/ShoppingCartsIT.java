package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import static com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartsPersistenceProvider;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({ShoppingCartsPersistenceProvider.class,
        ShoppingCartPersistenceEntityAssembler.class,
        ShoppingCartPersistenceEntityDisassembler.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class
})
class ShoppingCartsIT {


    private final ShoppingCarts shoppingCarts;
    private final Customers customers;

    @Autowired
    ShoppingCartsIT(ShoppingCarts shoppingCarts, Customers customers) {
        this.shoppingCarts = shoppingCarts;
        this.customers = customers;
    }

    @BeforeEach
    void setup() {
        if (!customers.exists(DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldPersistAndFind() {
        var originalShoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        shoppingCarts.add(originalShoppingCart);

        var possibleShoppingCart = shoppingCarts.ofId(originalShoppingCart.id());
        assertThat(possibleShoppingCart).isPresent();

        var savedShoppingCart = possibleShoppingCart.get();

        assertThat(savedShoppingCart).satisfies(shoppingCart -> {
           assertThat(shoppingCart.id()).isEqualTo(savedShoppingCart.id());
           assertThat(shoppingCart.customerId()).isEqualTo(savedShoppingCart.customerId());
           assertThat(shoppingCart.totalItems().value()).isEqualTo(savedShoppingCart.totalItems().value());
           assertThat(shoppingCart.items()).hasSameSizeAs(savedShoppingCart.items());
           assertThat(shoppingCart.id()).isEqualTo(savedShoppingCart.id());
        });
    }

    @Test
    void shouldFindShoppingCartByCustomer() {
        var originalShoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        shoppingCarts.add(originalShoppingCart);

        var possibleShoppingCart = shoppingCarts.ofCustomer(originalShoppingCart.customerId());
        assertThat(possibleShoppingCart).isPresent();
        var savedShoppingCart = possibleShoppingCart.get();

        assertThat(savedShoppingCart).satisfies(shoppingCart -> {
            assertThat(shoppingCart.id()).isEqualTo(savedShoppingCart.id());
            assertThat(shoppingCart.customerId()).isEqualTo(savedShoppingCart.customerId());
            assertThat(shoppingCart.totalItems().value()).isEqualTo(savedShoppingCart.totalItems().value());
            assertThat(shoppingCart.items()).hasSameSizeAs(savedShoppingCart.items());
            assertThat(shoppingCart.id()).isEqualTo(savedShoppingCart.id());
        });
    }

    @Test
    void shouldUpdateExistingShoppingCart() {
        var shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        shoppingCarts.add(shoppingCart);

        shoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        var beforeTotalItems  = shoppingCart.totalItems().value();
        var beforeTotalAmount = shoppingCart.totalAmount().value();
        var itemId            = shoppingCart.items().iterator().next().id();

        shoppingCart.changeItemQuantity(itemId, new Quantity(5));
        shoppingCarts.add(shoppingCart);

        shoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        assertThat(shoppingCart).isNotNull();
        assertThat(shoppingCart.totalItems().value()).isNotEqualTo(beforeTotalItems);
        assertThat(shoppingCart.totalAmount().value()).isNotEqualTo(beforeTotalAmount);
    }

    @Test
    void shouldCountExistingItems() {
        assertThat(shoppingCarts.count()).isZero();

        ShoppingCart cartT1 = ShoppingCartTestDataBuilder.aShoppingCart().build();
        ShoppingCart cartT2 = ShoppingCartTestDataBuilder.aShoppingCart().build();

        shoppingCarts.add(cartT1);
        shoppingCarts.add(cartT2);

        assertThat(shoppingCarts.count()).isEqualTo(2L);
    }

}