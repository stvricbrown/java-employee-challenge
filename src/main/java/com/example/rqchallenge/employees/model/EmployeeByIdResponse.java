package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeByIdResponse {

    private final Employee employee;

    private final String message;

    private final String status;

    @JsonCreator
    public EmployeeByIdResponse(@JsonProperty(value="data", required=true) Employee employee,
                    @JsonProperty(value="message", required=true) String message,
                    @JsonProperty(value="status", required=true) String status) {
        this.employee = employee;
        this.message = message;
        this.status = status;
    }

    @JsonGetter("data")
    public final Employee getEmployee() {
        return employee;
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
