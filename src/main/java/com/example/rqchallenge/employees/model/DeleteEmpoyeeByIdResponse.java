package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteEmpoyeeByIdResponse {

    private final String employeeId;

    private final String message;

    private final String status;

    @JsonCreator
    public DeleteEmpoyeeByIdResponse(@JsonProperty(value="data", required=true) String employeeId,
                                     @JsonProperty(value="message", required=true) String message,
                                     @JsonProperty(value="status", required=true) String status) {
        this.employeeId = employeeId;
        this.message = message;
        this.status = status;
    }

    @JsonGetter("data")
    public final String getEmployeeId() {
        return employeeId;
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
