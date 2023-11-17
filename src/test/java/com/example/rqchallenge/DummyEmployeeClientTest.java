package com.example.rqchallenge;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.MockRestServiceServer.createServer;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.example.DummyEmployeeClientTestConfig;
import com.example.rqchallenge.employees.dummy.client.DummyEmployeeClient;
import com.example.rqchallenge.employees.dummy.client.DummyEmployeeClientConfig;
import com.example.rqchallenge.employees.model.CreateResponse;
import com.example.rqchallenge.employees.model.DeleteEmpoyeeByIdResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeByIdResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    DummyEmployeeClientConfig.class,
    DummyEmployeeClientTestConfig.class
})
@TestPropertySource("DummyEmployeeClientTestConfig.properties")
class DummyEmployeeClientTest {

    private static String MOCK_SERVICE_URL = "http://localhost:8080/api/v1";

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
    private DummyEmployeeClient client;

    @Value("${dummy.restapiexample.baseURL}")
    private String baseTestUrl;

    private MockRestServiceServer mockServer;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    private void setUp() {
        assertThat("Must use mock server Url.", baseTestUrl, is(equalTo(MOCK_SERVICE_URL)));
        mockServer = createServer(restTemplate);
    }

    @ParameterizedTest
    @MethodSource("generateListOfEmployees")
    void testGetAllEmployees(List<Employee> expectedEmployees) {

        // Given
        String expectedMessage =  "Successfully! All records has been fetched.";
        String expectedStatus = "success";

        EmployeesResponse expectedEmployeesResponse =
                new EmployeesResponse(expectedEmployees, expectedMessage, expectedStatus);
        String testURL = format("%s/employees", baseTestUrl);
        URI testURI = makeTestUri(testURL);
        mockServer.expect(once(), requestTo(testURI)).andExpect(method(GET))
                                                     .andRespond(withStatus(OK)
                                                     .contentType(APPLICATION_JSON)
                                                     .body(writeTestValueAsJsonString(expectedEmployeesResponse)));

        // When
        List<Employee> actualEmployees = client.getAllEmployees().getBody();

        // Then
        mockServer.verify();
        assertThat("The actual employees should be the expected employees.",
                   actualEmployees, is(equalTo(expectedEmployees)));
    }

    @Test
    void testGetEmployeeById() {

        // Given
        int employeeId = 1;
        Employee expectedEmployee = new Employee(61, "Tiger Nixon", 320800, employeeId, "");
        String expectedMessage =  "Successfully! Record has been fetched.";
        String expectedStatus = "success";

        EmployeeByIdResponse expectedEmployeeByIdResponse =
                            new EmployeeByIdResponse(expectedEmployee, expectedMessage, expectedStatus);
        String testURL = format("%s/employee/%d", baseTestUrl, employeeId);
        URI testURI = makeTestUri(testURL);
        mockServer.expect(once(), requestTo(testURI)).andExpect(method(GET))
                                                     .andRespond(withStatus(OK)
                                                     .contentType(APPLICATION_JSON)
                                                     .body(writeTestValueAsJsonString(expectedEmployeeByIdResponse)));

        // When
        Employee actualEmployee = client.getEmployeeById(employeeId).getBody();

        // Then
        mockServer.verify();
        assertThat("The actual employee should be the expected employee",
                   actualEmployee, is(equalTo(expectedEmployee)));
    }

    @Test
    void testCreateEmployee() {

        // Given
        int expectedEmployeeId = 1821;
        int expectedAge = 99;
        String expectedName = "Fred Bloggs";
        int expectedSalary = 256000;
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("age", String.valueOf(expectedAge));
        employeeInput.put("name", expectedName);
        employeeInput.put("salary", String.valueOf(expectedSalary));
        Employee expectedEmployee = new Employee(expectedAge, expectedName, expectedSalary, expectedEmployeeId, "");
        String expectedMessage = "Successfully! Record has been added.";
        String expectedStatus = "success";

        CreateResponse expectedCreateResponse =
                            new CreateResponse(expectedEmployee, expectedMessage, expectedStatus);
        String testURL = format("%s/create", baseTestUrl);
        URI testURI = makeTestUri(testURL);
        mockServer.expect(once(), requestTo(testURI)).andExpect(method(POST))
                                                     .andRespond(withStatus(OK)
                                                     .contentType(APPLICATION_JSON)
                                                     .body(writeTestValueAsJsonString(expectedCreateResponse)));

        // When
        Employee actualEmployee = client.createEmployee(employeeInput).getBody();

        // Then
        mockServer.verify();
        assertThat("The actual employee should be the expected employee",
                   actualEmployee, is(equalTo(expectedEmployee)));
    }

    @Test
    void testDeleteEmployeeById() {

        // Given
        int employeeId = 1;
        String expectedEmployeeId = String.valueOf(employeeId);
        String expectedMessage =  "Successfully! Record has been deleted";
        String expectedStatus = "success";

        DeleteEmpoyeeByIdResponse expectedDeleteEmployeeByIdResponse =
                            new DeleteEmpoyeeByIdResponse(expectedEmployeeId, expectedMessage, expectedStatus);
        String testURL = format("%s/delete/%d", baseTestUrl, employeeId);
        URI testURI = makeTestUri(testURL);
        mockServer.expect(once(), requestTo(testURI)).andExpect(method(DELETE))
                                                     .andRespond(withStatus(OK)
                                                     .contentType(APPLICATION_JSON)
                                                     .body(writeTestValueAsJsonString(
                                                             expectedDeleteEmployeeByIdResponse)));

        // When
        String actualEmployeeId = client.deleteEmployeeById(employeeId).getBody();

        // Then
        mockServer.verify();
        assertThat("The actual employee Id should be the expected employee Id",
                   actualEmployeeId, is(equalTo(expectedEmployeeId)));
    }

    /**
     * Avoid having to specify {@code URISyntaxException} in the {@code throws} clause of every test method.
     */
    private URI makeTestUri(String testURL) {
        try {
            return new URI(testURL);
        } catch (URISyntaxException use) {
            fail(format("Invalid URI: %s. The error is %s", testURL, use.getMessage()));

            // Keep the compiler happy. This line will never be executed.
            return null;
        }
    }


    /**
     * Avoid having to specify {@code JsonProcessingException} in the {@code throws} clause of every test method.
     */
    private <T> String writeTestValueAsJsonString(T testValue) {

        try {
            return mapper.writeValueAsString(testValue);
        } catch (JsonProcessingException jpe) {
            fail(format("Failed to serialize %s to JSON. The error is %s", testValue, jpe.getMessage()));

            // Keep the compiler happy. This line will never be executed.
            return null;
        }
    }

}
