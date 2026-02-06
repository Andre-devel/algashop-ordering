package com.algaworks.algashop.ordering.presentation.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.customer.CustomerPersistenceEntityTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utils.AlgaShopResourceUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ShoppingCartControllerIT {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private ShoppingCartPersistenceEntityRepository shoppingCartRepository;

    @Autowired
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    private static final UUID validCustomerId = UUID.fromString("6e148bd5-47f6-4012-b9da-02cfaa294f7a");

    private WireMockServer wireMockProductCatalog;

    @BeforeEach
    public void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;

        RestAssured.config().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));

        initDatabase();

        wireMockProductCatalog = new WireMockServer(options()
                .port(8781)
                .usingFilesUnderDirectory("src/test/resources/wiremock/product-catalog")
                .extensions(new ResponseTemplateTransformer(true)));


        wireMockProductCatalog.start();
    }

    @AfterEach
    public void after() {
        wireMockProductCatalog.stop();
    }

    private void initDatabase() {
        customerPersistenceEntityRepository.saveAndFlush(
                CustomerPersistenceEntityTestDataBuilder.existingCustomer().id(validCustomerId).build()
        );
    }
    
    @Test
    public void shouldCreateShoppingCart() {
        String json = AlgaShopResourceUtils.readContent("json/create-shopping-cart.json");

        String createdShoppingCartId = RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.shopping-cart.v1+json")
                .body(json)
                .when()
                .post("/api/v1/shopping-carts")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString())).extract()
                .jsonPath().getString("id");

        boolean shoppingCartExistis = shoppingCartRepository.existsById(UUID.fromString(createdShoppingCartId));
        Assertions.assertThat(shoppingCartExistis).isTrue();
    }
    
    @Test
    public void shouldAddItemToShoppingCart() {
        String jsonCreateShoppingCart = AlgaShopResourceUtils.readContent("json/create-shopping-cart.json");

        String createdShoppingCartId = RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.shopping-cart.v1+json")
                .body(jsonCreateShoppingCart)
                .when()
                .post("/api/v1/shopping-carts")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString())).extract()
                .jsonPath().getString("id");
        
        String json = AlgaShopResourceUtils.readContent("json/add-shopping-cart-item.json");

        RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(json)
                .when()
                .post(String.format("/api/v1/shopping-carts/%s/items", createdShoppingCartId))
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        var shoppingCart = shoppingCartRepository.findByIdWithItems(UUID.fromString(createdShoppingCartId)).orElseThrow();
        
        Assertions.assertThat(shoppingCart.getTotalItems()).isEqualTo(1);
        Assertions.assertThat(shoppingCart.getItems().stream().findFirst().get().getPrice()).isEqualByComparingTo(new BigDecimal(1000));
    }
}
