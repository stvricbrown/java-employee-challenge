package com.example.rqchallenge.employees.dummy.client;

import static java.time.Duration.ofSeconds;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.example.rqchallenge.employees.model.Employee;

@Component
public class DummyEmployeeClient {


    private String baseURL;

    private final RestTemplate restTemplate;

    public DummyEmployeeClient(RestTemplateBuilder restTemplateBuilder,
                               @Value("${dummy.restapiexample.baseURL:https://dummy.restapiexample.com/api/v1")
                               String baseURL,
                               @Value("${dummy.restapiexample.connectionTimeout:10}")
                               int connectionTimeout,
                               @Value("${dummy.restapiexample.requestTimeout:5}")
                               int requestTimeout) {
        this.baseURL = baseURL;
        this.restTemplate = buildRestTemplate(restTemplateBuilder, connectionTimeout, requestTimeout);
    }

    private RestTemplate buildRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                           int connectionTimeout,
                                           int requestTimeout) {
        RestTemplate template = restTemplateBuilder.setConnectTimeout(ofSeconds(connectionTimeout))
                                                   .setReadTimeout(ofSeconds(requestTimeout))
                                                   .errorHandler(new DummyEmployeeErrorHandler())
                                                   .build();

        template.setUriTemplateHandler(new DefaultUriBuilderFactory(baseURL));
        return template;
    }

    public ResponseEntity<List<Employee>> getAllEmployees() {
        ParameterizedTypeReference<List<Employee>> responseType = new ParameterizedTypeReference<>() {};
        return restTemplate.exchange("/employees", GET, null, responseType);
    }

    public ResponseEntity<Employee> getEmployeeById(int employeeId) {
        ParameterizedTypeReference<Employee> responseType = new ParameterizedTypeReference<>() {};
        return restTemplate.exchange("/employee/{id}",
                             GET,
                      null,
                                     responseType,
                      employeeId);
    }

    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        ParameterizedTypeReference<Employee> responseType = new ParameterizedTypeReference<>() {};
        return restTemplate.exchange("/create",
                             POST,
                      null,
                                     responseType,
                       employeeInput);
    }

    public ResponseEntity<String> deleteEmployeeById(int employeeId) {
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {};
        return restTemplate.exchange("/delete",
                             DELETE,
                      null,
                                     responseType,
                                     employeeId);
    }

}
