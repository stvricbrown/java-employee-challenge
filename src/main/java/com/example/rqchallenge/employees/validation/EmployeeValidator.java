package com.example.rqchallenge.employees.validation;

import static java.lang.Integer.parseUnsignedInt;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.containsOnly;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class EmployeeValidator {

    public static int validateEmployeeId(String value) {
        requireNonNull(value, "The employee Id must not be null.");

        if (isEmpty(value)) {
            throw new IllegalArgumentException("The employee Id must not be empty.");
        }
        try {
            //  Don't want to use a regular expression, or allow whitespace, non-ascii digits, or sign characters.
            if (containsOnly(value, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')) {
                String message = format("The value \"%s\" is not a valid employee Id because it contains non-digits.",
                                        value);
                throw new IllegalArgumentException(message);
            }

            // This fails only if the Id is more than 32 bits in magnitude.
            return parseUnsignedInt(value, 10);
        } catch (NumberFormatException nfe) {
            String message = format("The value \"%s\" is not a valid employee Id.", value);
            throw new IllegalArgumentException(message, nfe);
        }
    }
}
