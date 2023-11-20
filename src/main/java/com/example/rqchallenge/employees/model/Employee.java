package com.example.rqchallenge.employees.model;

import static java.lang.Integer.compareUnsigned;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import java.util.Comparator;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Employee implements Comparable<Employee>, Comparator<Employee> {

    private static final int HASHCODE_MULTIPLIER = 41;

    private static final int INITIAL_HASHCODE_VALUE = 31;

    private final int age;

    private final String name;

    private final int salary;

    private final int id;

    private final String profileImage;

    @JsonIgnore
    private final int hashCode;

    /**
     * Needs to sort in reverse order (descending) to allow the top 10 highest salaries.
     */
    public static int compareSalaryDescending(Employee lhs, Employee rhs) {
        return compareUnsigned(rhs.getSalary(), lhs.getSalary());
    }

    @JsonCreator
    public Employee(@JsonProperty(value="employee_age", required=true) int age,
                    @JsonProperty(value="employee_name", required=true) String name,
                    @JsonProperty(value="employee_salary", required=true) int salary,
                    @JsonProperty(value="id", required=true) int id,
                    @JsonProperty("profile_image") String profileImage) {
        this.age = age;
        this.name = name;
        this.salary = salary;
        this.id = id;
        this.profileImage = profileImage;
        this.hashCode = INITIAL_HASHCODE_VALUE * HASHCODE_MULTIPLIER + id;
    }

    @JsonGetter("employee_age")
    public final int getAge() {
        return age;
    }

    @JsonGetter("employee_name")
    public final String getName() {
        return name;
    }

    @JsonGetter("employee_salary")
    public final int getSalary() {
        return salary;
    }

    @JsonGetter("id")
    public final int getId() {
        return id;
    }

    @JsonGetter("profile_image")
    public final String getProfileImage() {
        return profileImage;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Employee)) {
            return false;
        }

        Employee otherEmployee = Employee.class.cast(other);

        // Employee Id's are unique.
        return this.id == otherEmployee.id;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, SHORT_PREFIX_STYLE);

        return builder.append("age", age)
                      .append("name", name)
                      .append("salary", salary)
                      .append("id", id)
                      .append("profileImage", profileImage)
                      .build();
    }

    @Override
    public int compareTo(Employee otherEmployee) {
        return compareUnsigned(id, otherEmployee.id);
    }

    @Override
    public int compare(Employee lhs, Employee rhs) {
        requireNonNull(lhs, "The lhs Employee must not be null.");
        requireNonNull(rhs, "The rhs Employee must not be null.");
        return compareUnsigned(lhs.id, rhs.id);
    }
}
