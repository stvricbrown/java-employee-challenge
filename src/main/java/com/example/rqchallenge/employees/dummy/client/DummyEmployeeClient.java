package com.example.rqchallenge.employees.dummy.client;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.ok;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.rqchallenge.employees.model.CreateResponse;
import com.example.rqchallenge.employees.model.DeleteEmpoyeeByIdResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeByIdResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;

@Component
public class DummyEmployeeClient {

    private final RestTemplate restTemplate;

    public DummyEmployeeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<List<Employee>> getAllEmployees() {
        ParameterizedTypeReference<EmployeesResponse> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<EmployeesResponse> responseEntity =
                restTemplate.exchange("/employees", GET, null, responseType);
        EmployeesResponse response = responseEntity.getBody();
        return ok(response.getEmployees());
   }

    public ResponseEntity<Employee> getEmployeeById(int employeeId) {
        ParameterizedTypeReference<EmployeeByIdResponse> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<EmployeeByIdResponse> responseEntity =
                restTemplate.exchange("/employee/{id}", GET, null, responseType, employeeId);
        EmployeeByIdResponse response = responseEntity.getBody();

        if (response.getEmployee() != null) {
            return ok(response.getEmployee());
        }

        // The "real" service returns HTTP status 200 with a null data field when the employee does not exist.
        return new ResponseEntity<>(NOT_FOUND);
    }

    public ResponseEntity<Employee> createEmployee(Map<String, Object> employeeInput) {
        ParameterizedTypeReference<CreateResponse> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<CreateResponse> responseEntity =
                restTemplate.exchange("/create", POST,  null, responseType, employeeInput);
        CreateResponse response = responseEntity.getBody();

        return ok(response.getEmployee());
    }

    public ResponseEntity<String> deleteEmployeeById(int employeeId) {
        ParameterizedTypeReference<DeleteEmpoyeeByIdResponse> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<DeleteEmpoyeeByIdResponse> responseEntity =
                restTemplate.exchange("/delete/{id}", DELETE, null, responseType, employeeId);
        DeleteEmpoyeeByIdResponse response = responseEntity.getBody();

        // In some cases, the "real" service can return only an error message, i.e. it does not return a status, or
        // an employee Id.
        if (response.getMessage().toLowerCase().contains("error")) {
            return  new ResponseEntity<>(null, BAD_REQUEST);
        }

        // Otherwise, a delete request is always successful.
        return ok(response.getEmployeeId());
    }

}
