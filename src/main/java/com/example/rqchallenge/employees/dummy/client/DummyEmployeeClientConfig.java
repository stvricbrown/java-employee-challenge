package com.example.rqchallenge.employees.dummy.client;

import static java.time.Duration.ofSeconds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class DummyEmployeeClientConfig {

    @Value("${dummy.restapiexample.baseURL:https://dummy.restapiexample.com/api/v1}")
    private String baseURL;

    @Value("${dummy.restapiexample.connectionTimeout:10}")
    private int connectionTimeout;

    @Value("${dummy.restapiexample.requestTimeout:5}")
    private int requestTimeout;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate =  restTemplateBuilder.setConnectTimeout(ofSeconds(connectionTimeout))
                                                        .setReadTimeout(ofSeconds(requestTimeout))
                                                        .errorHandler(new DummyEmployeeErrorHandler())
                                                        .build();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseURL));
        return restTemplate;
    }
}
