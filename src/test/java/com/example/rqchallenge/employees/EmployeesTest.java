package com.example.rqchallenge.employees;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.client.MockRestServiceServer.createServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.example.DummyEmployeeClientTestConfig;
import com.example.rqchallenge.employees.dummy.client.DummyEmployeeClient;
import com.example.rqchallenge.employees.dummy.client.DummyEmployeeClientConfig;
import com.example.rqchallenge.employees.model.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    DummyEmployeeClientConfig.class,
    DummyEmployeeClientTestConfig.class
})
@TestPropertySource("classpath:DummyEmployeeClientTestConfig.properties")
public class EmployeesTest {

    protected static String MOCK_SERVICE_URL = "http://localhost:8080/api/v1";

    static Stream<Arguments> generateListOfEmployees() {
        List<Employee> employees = new ArrayList<>();

        employees.add(new Employee(61, "Tiger Nixon", 320800, 1, ""));
        employees.add(new Employee(63, "Garrett Winters", 170750, 2, ""));
        return Stream.of(
            Arguments.of(employees)
        );
    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    protected DummyEmployeeClient client;

    @Value("${dummy.restapiexample.baseURL}")
    protected String baseTestUrl;

    protected MockRestServiceServer mockServer;

    protected ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    private void setUp() {
        assertThat("Must use mock server Url.", baseTestUrl, is(equalTo(MOCK_SERVICE_URL)));
        mockServer = createServer(restTemplate);
    }

    /**
     * Avoid having to specify {@code URISyntaxException} in the {@code throws} clause of every test method.
     */
    protected URI makeTestUri(String testURL) {
        try {
            return new URI(testURL);
        } catch (URISyntaxException use) {
            fail(format("Invalid URI: %s. The error is %s", testURL, use.getMessage()));

            // Keep the compiler happy. This line will never be executed.
            return null;
        }
    }

    protected String readFileFromClasspath(String resourcePath) {

        try (InputStream input = new ClassPathResource(resourcePath).getInputStream()) {
             return new String(input.readAllBytes(), UTF_8);
        } catch (IOException ioe) {
            String message = format("Failed to read %s from the classpath.");
            fail(message, ioe);
            return null;
        }
    }

    /**
     * Avoid having to specify {@code JsonProcessingException} in the {@code throws} clause of every test method.
     */
    protected <T> String writeTestValueAsJsonString(T testValue) {

        try {
            return mapper.writeValueAsString(testValue);
        } catch (JsonProcessingException jpe) {
            fail(format("Failed to serialize %s to JSON. The error is %s", testValue, jpe.getMessage()));

            // Keep the compiler happy. This line will never be executed.
            return null;
        }
    }

}
