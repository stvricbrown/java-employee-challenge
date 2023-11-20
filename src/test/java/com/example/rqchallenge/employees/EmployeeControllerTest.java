package com.example.rqchallenge.employees;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.sort;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.in;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.ok;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;

import com.example.rqchallenge.employees.dummy.client.DummyEmployeeClient;
import com.example.rqchallenge.employees.model.Employee;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest extends EmployeesTest {

    EmployeeController employeeController;

    private static List<Employee> employeeList;

    @Mock
    DummyEmployeeClient mockClient;

    private Employee employee1;

    private Employee employee2;

    @BeforeAll
    void setUpEmployees() {

        employeeList = new ArrayList<>();

        employee1 = new Employee(61, "Tiger Nixon", 320800, 1, "");
        employeeList.add(employee1);
        employee2 = new Employee(63, "Garrett Winters", 170750, 2, "");
        employeeList.add(employee2);
    }

    Stream<Arguments> generateExpectedEmployees() {

        List<Employee> tigerNixonList = new ArrayList<>();
        tigerNixonList.add(employee1);

        List<Employee> garretWintersList = new ArrayList<>();
        garretWintersList.add(employee2);

        List<Employee> bothEmployees = new ArrayList<>();
        bothEmployees.add(employee1);
        bothEmployees.add(employee2);


        List<Employee> noEmployees = new ArrayList<>();

        return Stream.of(
            Arguments.of("Garrett Winters", garretWintersList),
            Arguments.of("Tiger Nixon", tigerNixonList),
            Arguments.of("n", bothEmployees),
            Arguments.of("z", noEmployees)
        );
    }

    Stream<Arguments> generateBadEmployeeIds() {

        String idWithInvalidCharacter = "-1234567";
        String idWithTooManyDigits = "12345678901234567890";
        String emptyId = "";

        return Stream.of(
                Arguments.of(idWithInvalidCharacter,
                             BAD_REQUEST,
                             format("The value \"%s\" is not a valid employee Id because it contains non-digits.",
                                    idWithInvalidCharacter)),
                Arguments.of(idWithTooManyDigits,
                        BAD_REQUEST,
                        format("The value \"%s\" is not a valid employee Id because there are too many digits.",
                               idWithTooManyDigits)),
                Arguments.of(emptyId, BAD_REQUEST, "The employee Id must not be empty.")
        );
    }

    Stream<Arguments> generateHighestSalaryArguments() {

        List<Employee> longerList = makeLongerListOfEmployees();

        return Stream.of(Arguments.of(new ArrayList<>(), 0),
                         Arguments.of(employeeList, employee1.getSalary()),
                         Arguments.of(longerList, 9000000)
        );
    }

    Stream<Arguments> generateTenHighestSalaryArguments() {

        List<Employee> longerList = makeLongerListOfEmployees();

        return Stream.of(Arguments.of(new ArrayList<>()),
                         Arguments.of(employeeList),
                         Arguments.of(longerList)
        );
    }



    @BeforeEach
    void setUp() {
        employeeController = new EmployeeController(mockClient);
    }

    /**
     * For some reason, this test won't run in Eclipse 2023-09. It does run with gradle test.
     */
    @ParameterizedTest
    @MethodSource("generateExpectedEmployees")
    void testGetEmployeesByNameSearchString(String name, List<Employee> expectedEmployees) {

        // Given
        when(mockClient.getAllEmployees()).thenReturn(ok(employeeList));

        // When
        ResponseEntity<List<Employee>> actualResponse = employeeController.getEmployeesByNameSearch(name);

        List<Employee> actualEmployees = actualResponse.getBody();

        // Then
        assertThat("The actual employees must be the same size as the expected employees.",
                    actualEmployees, hasSize(expectedEmployees.size()));

        assertThat("The actual employees must be the expected employees.",
                   actualEmployees, everyItem(is(in(expectedEmployees))));
    }

    @Test
    void testGetEmployeeByIdOK() {

        // Given
        String testId = "1";
        Employee expectedEmployee = employee1;
        when(mockClient.getEmployeeById(Integer.valueOf(testId))).thenReturn(ok(expectedEmployee));

        // When
        ResponseEntity<Employee> actualResponse = employeeController.getEmployeeById(testId);

        Employee actualEmployee = actualResponse.getBody();

        // Then
        assertThat("The actual employee must be the expected employee.",
                   actualEmployee, is(equalTo(expectedEmployee)));
    }

    @Test
    void testGetEmployeeByIdNoSuchId() {

        // Given
        String testId = "3";

        when(mockClient.getEmployeeById(Integer.valueOf(testId))).thenReturn(new ResponseEntity<>(NOT_FOUND));

        // When
        ResponseEntity<Employee> actualResponse = employeeController.getEmployeeById(testId);

        // Then
        assertThat("The the response status code is NOT_FOUND.",
                   actualResponse.getStatusCode(), is(equalTo(NOT_FOUND)));
    }

    @ParameterizedTest
    @MethodSource("generateBadEmployeeIds")
    void testGetEmployeeByIdBadId(String testId, HttpStatus expectedStatusCode, String expectedMessage) {

        // Given

        // When
        HttpServerErrorException actualException =
                assertThrows(HttpServerErrorException.class, () -> employeeController.getEmployeeById(testId));
        HttpStatus actualStatusCode = actualException.getStatusCode();
        String actualMessage = actualException.getResponseBodyAsString(UTF_8);

        // Then
        assertThat("The response status code is the expected status code.",
                   actualStatusCode, is(equalTo(expectedStatusCode)));

        assertThat("The actual message is the expected message.", actualMessage, is(equalTo(expectedMessage)));
    }

    @ParameterizedTest
    @MethodSource("generateHighestSalaryArguments")
    void testGetHighestSalaryOfEmployees(List<Employee> testEmployeeList, int expectedHighestSalary) {

        // Given
        when(mockClient.getAllEmployees()).thenReturn(ok(testEmployeeList));

        // When
        ResponseEntity<Integer> actualResponse = employeeController.getHighestSalaryOfEmployees();

        int actualHighestSalary = actualResponse.getBody();

        // Then
        assertThat("The actual highest salary must be the expected highest salary.",
                    actualHighestSalary, is(equalTo(expectedHighestSalary)));
    }


    @ParameterizedTest
    @MethodSource("generateTenHighestSalaryArguments")
    void testGetTopTenHighestEarningEmployeeNames() {

        // Given
        List<Employee> testEmployeeList = makeLongerListOfEmployees();
        List<String> exepectedTopTenNames = makeTopTenList(testEmployeeList);
        when(mockClient.getAllEmployees()).thenReturn(ok(testEmployeeList));

        // When
        ResponseEntity<List<String>> actualResponse = employeeController.getTopTenHighestEarningEmployeeNames();

        List<String> actualTopTen = actualResponse.getBody();

        // Then
        assertThat("The actual top Ten names must be the expected top ten names.",
                   actualTopTen, is(equalTo(exepectedTopTenNames)));
    }

    @Disabled
    @Test
    void testCreateEmployeeOK() {

        // Given
        Map<String, Object> testArguments = new HashMap<>();
        String testName = "John Loius Stevenson";
        testArguments.put("name", testName);
        String testAge = "19";
        testArguments.put("age", testAge);
        String testSalary = "200";
        testArguments.put("salary", testSalary);
        int testId = 31415926;
        Employee expectedNewEmployee = new Employee(Integer.valueOf(testAge),
                                                    testName,
                                                    Integer.valueOf(testSalary),
                                                    testId,
                                                    "");

        when(mockClient.createEmployee(testArguments)).thenReturn(ok(expectedNewEmployee));

        // When
        ResponseEntity<Employee> actualResponse = employeeController.createEmployee(testArguments);
        Employee actualEmployee = actualResponse.getBody();

        // Then

        // Note: Equality is based on Id only.
        assertThat("The new employee must be the expected employee.",
                   actualEmployee, is(equalTo(expectedNewEmployee)));

        assertThat("The new employee's name must be the expected name.",
                   actualEmployee.getName(), is(equalTo(expectedNewEmployee.getName())));

        assertThat("The new employee's age must be the expected age.",
                   actualEmployee.getAge(), is(equalTo(expectedNewEmployee.getAge())));

        assertThat("The new employee's salaray must be the expected salary.",
                   actualEmployee.getSalary(), is(equalTo(expectedNewEmployee.getSalary())));

    }

    @Disabled
    @Test
    void testDeleteEmployeeById() {

        // Given
        int testId = 31415926;
        String expectedId = String.valueOf(testId);

        when(mockClient.deleteEmployeeById(testId)).thenReturn(ok(expectedId));

        // When
        ResponseEntity<String> actualResponse = employeeController.deleteEmployeeById(expectedId);
        String actualId = actualResponse.getBody();

        // Then
        assertThat("The actual Id must be the expected Id.", actualId, is(equalTo(expectedId)));
    }

    private List<Employee> makeLongerListOfEmployees() {
        List<Employee> longerList = new ArrayList<>();
        longerList.addAll(employeeList);
        longerList.add(new Employee(72, "Frank Sinatra", 2000000, 3, ""));
        longerList.add(new Employee(75, "Dean Martin",   5000000, 4, ""));
        longerList.add(new Employee(80, "Sammy Davis Junior", 4999999, 5, ""));
        longerList.add(new Employee(90, "Tony Bennett", 3000000, 6, ""));
        longerList.add(new Employee(75, "Liberace", 5000000, 7, ""));
        longerList.add(new Employee(72, "Elton John", 7000000, 8, ""));
        longerList.add(new Employee(80, "Barbara Streisland", 8000000, 9, ""));
        longerList.add(new Employee(81, "Vera Cruz", 9000000, 10, ""));
        longerList.add(new Employee(81, "Shirley Bassey", 7500000, 11, ""));
        longerList.add(new Employee(81, "Diana Dors", 2000000, 12, ""));
        longerList.add(new Employee(78, "Eric Clapton", 1234567, 13, ""));
        return longerList;
    }

    private List<String> makeTopTenList(List<Employee> employees) {
        List<Employee> sortedList = new ArrayList<>(employees.size());
        sortedList.addAll(employees);
        sort(sortedList, Employee::compareSalaryDescending);
        return  sortedList.stream().limit(10).map(Employee::getName).collect(toUnmodifiableList());
   }
}
