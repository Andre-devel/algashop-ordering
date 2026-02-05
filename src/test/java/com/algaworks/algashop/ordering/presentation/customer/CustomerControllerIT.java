package com.algaworks.algashop.ordering.presentation.customer;

import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import com.algaworks.algashop.ordering.utils.AlgaShopResourceUtils;
import io.restassured.RestAssured;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CustomerControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private CustomerPersistenceEntityRepository customerPersistenceEntityRepository;

    @BeforeEach
    void setUp() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
    }
    
    @Test
    public void shouldCreateCustomer() {
        String json  = AlgaShopResourceUtils.readContent("json/create-customer.json");
        
        String createdCustomerId = RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString())).extract()
                .jsonPath().getString("id");
        
        boolean customerExistis = customerPersistenceEntityRepository.existsById(UUID.fromString(createdCustomerId));
        Assertions.assertThat(customerExistis).isTrue();
    }
    
    @Test
    public void shouldNotCreateCustomerWhenIsNotValid() {
        String json  = AlgaShopResourceUtils.readContent("json/create-customer-without-first-name.json");
        
        RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
    
    @Test
    public void shouldDeleteCustomer() {
        String json  = AlgaShopResourceUtils.readContent("json/create-customer.json");
        
        String createdCustomerId = RestAssured
                .given()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType("application/vnd.order-with-product.v1+json")
                .body(json)
                .when()
                .post("/api/v1/customers")
                .then()
                .assertThat()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.not(Matchers.emptyString())).extract()
                .jsonPath().getString("id");
        
        RestAssured
                .given()
                .when()
                .delete("/api/v1/customers/{customerId}", createdCustomerId)
                .then()
                .assertThat()
                .statusCode(HttpStatus.NO_CONTENT.value());

        Optional<CustomerPersistenceEntity> byId = customerPersistenceEntityRepository.findById(UUID.fromString(createdCustomerId));
        boolean customerIsArchived = byId.isPresent() && byId.get().isArchived();
        Assertions.assertThat(customerIsArchived).isTrue();
    }
}