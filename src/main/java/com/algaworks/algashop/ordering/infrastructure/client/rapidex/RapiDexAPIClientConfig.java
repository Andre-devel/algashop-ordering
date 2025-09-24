package com.algaworks.algashop.ordering.infrastructure.client.rapidex;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RapiDexAPIClientConfig {
    @Bean
    public RapiDexAPIClient rapiDexAPIClient(
            RestClient.Builder builder,
            @Value("${algashop.integration.rapidex.url}") String apiUrl) {
        RestClient restClient = builder.baseUrl(apiUrl).build();
        RestClientAdapter restClientAdapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(restClientAdapter).build();
        return proxyFactory.createClient(RapiDexAPIClient.class);
    }
}
