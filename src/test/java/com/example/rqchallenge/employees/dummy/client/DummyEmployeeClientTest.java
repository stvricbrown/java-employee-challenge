package com.example.rqchallenge.employees.dummy.client;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.rqchallenge.employees.EmployeesTest;
import com.example.rqchallenge.employees.dummy.exceptions.ServerBusyException;
import com.example.rqchallenge.employees.model.CreateResponse;
import com.example.rqchallenge.employees.model.DeleteEmpoyeeByIdResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeByIdResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;

class DummyEmployeeClientTest extends EmployeesTest {
    private static String TOO_MANY_REQUESTS_PAGE = "/tooManyRequestsPage.html";

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
    void testTooManyRequests() {

        // Given
        String expectedMessage = format("The server reported \"%d: Too many requests.\"", TOO_MANY_REQUESTS.value());

        int employeeId = 1;
        String testURL = format("%s/employee/%d", baseTestUrl, employeeId);
        URI testURI = makeTestUri(testURL);
        String tooManyRequestsHtml = readFileFromClasspath(TOO_MANY_REQUESTS_PAGE);
        mockServer.expect(once(), requestTo(testURI)).andExpect(method(GET))
                                                     .andRespond(withStatus(TOO_MANY_REQUESTS)
                                                     .contentType(TEXT_HTML)
                                                     .body(tooManyRequestsHtml));

        // When
        ServerBusyException actualException = assertThrows(ServerBusyException.class, () -> {
            client.getEmployeeById(employeeId);
        });

        // Then
        mockServer.verify();

        String actualMessage = actualException.getMessage();
        assertThat("The exception message must be the expected exception message",
                   actualMessage, is(equalTo(expectedMessage)));
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
    void testGetEmployeeByIdNotFound() {

        // Given
        int employeeId = 1;
        Employee expectedEmployee = null;
        String expectedMessage =  "Successfully! Record has been fetched.";
        String expectedStatus = "success";

        EmployeeByIdResponse expectedEmployeeByIdResponse =
                            new EmployeeByIdResponse(expectedEmployee, expectedMessage, expectedStatus);
        String testURL = format("%s/employee/%d", baseTestUrl, employeeId);
        URI testURI = makeTestUri(testURL);
        mockServer.expect(once(), requestTo(testURI)).andExpect(method(GET))
                                                     .andRespond(withStatus(NOT_FOUND)
                                                     .contentType(APPLICATION_JSON)
                                                     .body(writeTestValueAsJsonString(expectedEmployeeByIdResponse)));

        // When
        ResponseEntity<Employee> response = client.getEmployeeById(employeeId);

        // Then
        mockServer.verify();
        Employee actualEmployee = response.getBody();
        assertThat("The actual employee should be the expected employee",
                   actualEmployee, is(equalTo(expectedEmployee)));

        HttpStatus actualStatus = response.getStatusCode();
        assertThat("The status code must be NOT_FOUND", actualStatus, is(equalTo(NOT_FOUND)));
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

    @Test
    void testDeleteEmployeeByIdError() {

        // Given
        int employeeId = 1;
        String expectedMessage =  "An error occurred";

        DeleteEmpoyeeByIdResponse expectedDeleteEmployeeByIdResponse =
                            new DeleteEmpoyeeByIdResponse(null, expectedMessage, null);
        String testURL = format("%s/delete/%d", baseTestUrl, employeeId);
        URI testURI = makeTestUri(testURL);
        mockServer.expect(once(), requestTo(testURI)).andExpect(method(DELETE))
                                                     .andRespond(withStatus(BAD_REQUEST)
                                                     .contentType(APPLICATION_JSON)
                                                     .body(writeTestValueAsJsonString(
                                                             expectedDeleteEmployeeByIdResponse)));

        // When
        ResponseEntity<String> response = client.deleteEmployeeById(employeeId);
        String actualEmployeeId = response.getBody();

        // Then
        mockServer.verify();

        assertThat("The actual employee Id should be null", actualEmployeeId, is(nullValue()));
        HttpStatus actualHttpStatus = response.getStatusCode();
        assertThat("The status code must be BAD_REQUEST", actualHttpStatus, is(equalTo(BAD_REQUEST)));
    }
}
