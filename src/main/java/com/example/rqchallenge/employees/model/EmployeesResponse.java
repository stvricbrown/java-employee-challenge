package com.example.rqchallenge.employees.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeesResponse {

    private final List<Employee> employees;

    private final String message;

    private final String status;

    @JsonCreator
    public EmployeesResponse(@JsonProperty(value="data", required=true) List<Employee> employees,
                    @JsonProperty(value="message", required=true) String message,
                    @JsonProperty(value="status", required=true) String status) {
        this.employees = employees;
        this.message = message;
        this.status = status;
    }

    @JsonGetter("data")
    public final List<Employee> getEmployees() {
        return employees;
    }

    @JsonGetter("message")
    public final String getMessage() {
        return message;
    }

    @JsonGetter("status")
    public final String getStatus() {
        return status;
    }
}
