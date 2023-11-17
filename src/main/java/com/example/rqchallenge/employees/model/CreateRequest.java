package com.example.rqchallenge.employees.model;

import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateRequest {

    private final String age;

    private final String name;

    private final String salary;

    @JsonCreator
    public CreateRequest(Map<String, String> employeeInput) {
        this.age = employeeInput.get("age");
        this.name = employeeInput.get("name");
        this.salary = employeeInput.get("salary");
    }

    public final String getAge() {
        return age;
    }

    public final String getName() {
        return name;
    }

    public final String getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, SHORT_PREFIX_STYLE);

        return builder.append("age", age)
                      .append("name", name)
                      .append("salary", salary)
                      .build();
    }
}
