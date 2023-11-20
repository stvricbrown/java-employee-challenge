package com.example.rqchallenge.employees;

import static com.example.rqchallenge.employees.validation.EmployeeValidator.validateEmployeeId;
import static com.example.rqchallenge.employees.validation.EmployeeValidator.validateEmployeeInput;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.http.HttpStatus.OK;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.rqchallenge.employees.dummy.client.DummyEmployeeClient;
import com.example.rqchallenge.employees.model.Employee;

public class EmployeeController implements IEmployeeController {

    private DummyEmployeeClient dummyEmployeeClient;

    public EmployeeController(DummyEmployeeClient dummyEmployeeClient) {
        this.dummyEmployeeClient = dummyEmployeeClient;
    }

    /**
     * For Mockito spy.
     */
    EmployeeController() {
        // Do nothing.
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return dummyEmployeeClient.getAllEmployees();
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {

        if (isBlank(searchString)) {
            throw new IllegalArgumentException("The search string must not be null, empty, or blank");
        }

        List<Employee> allEmployees = getListOfEmployees();

        List<Employee> responseBody = allEmployees.stream()
                                                  .filter(employee -> employee.getName().contains(searchString))
                                                  .collect(toUnmodifiableList());
        return new ResponseEntity<>(responseBody, OK);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        int employeeId = validateEmployeeId(id);
        return dummyEmployeeClient.getEmployeeById(employeeId);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        List<Employee> allEmployees = getListOfEmployees();

        int highestSalary = 0;

        // Avoid having to handle an empty Optional<Employee>.
        if (!allEmployees.isEmpty()) {
            Comparator<Employee> salaryComparator = comparing(Employee::getSalary);
            highestSalary = allEmployees.stream().max(salaryComparator).get().getSalary();
        }

        return new ResponseEntity<>(highestSalary, OK);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<Employee> allEmployees = getListOfEmployees();

        List<String> responseBody = allEmployees.stream().sorted(Employee::compareSalaryDescending)
                                                         .limit(10)
                                                         .map(Employee::getName)
                                                         .collect(toUnmodifiableList());


        return new ResponseEntity<>(responseBody, OK);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        validateEmployeeInput(employeeInput);
        return dummyEmployeeClient.createEmployee(employeeInput);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        int employeeId = validateEmployeeId(id);
        return dummyEmployeeClient.deleteEmployeeById(employeeId);
    }
    private List<Employee> getListOfEmployees() {
        return getAllEmployees().getBody();
    }

}
