package com.example.rqchallenge.employees.validation;

import static java.lang.Integer.parseUnsignedInt;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.containsOnly;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.Map;

import org.springframework.web.client.HttpServerErrorException;

public class EmployeeValidator {

    public static int validateEmployeeId(String value) {
        requireNonNull(value, "The employee Id must not be null.");

        if (isEmpty(value)) {
            String message = "The employee Id must not be empty.";
            throw new HttpServerErrorException(BAD_REQUEST, "Invalid Id", message.getBytes(UTF_8), UTF_8);
        }
        try {
            //  Don't want to use a regular expression, or allow whitespace, non-ascii digits, or sign characters.
            if (!containsOnly(value, '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')) {
                String message = format("The value \"%s\" is not a valid employee Id because it contains non-digits.",
                                        value);
                throw new HttpServerErrorException(BAD_REQUEST, "Invalid Id", message.getBytes(UTF_8), UTF_8);
            }

            // This fails only if the Id is more than 32 bits in magnitude.
            return parseUnsignedInt(value, 10);
        } catch (NumberFormatException nfe) {
            String message =
                    format("The value \"%s\" is not a valid employee Id because there are too many digits.", value);
            throw new HttpServerErrorException(BAD_REQUEST, "Invalid Id", message.getBytes(UTF_8), UTF_8);
        }
    }

    public static void validateEmployeeInput(Map<String, Object> employeeInput) {
        requireNonNull(employeeInput, "The employee input map must not be null.");

        validateMapHasKey(employeeInput, "age", "The empoyee input map must have an \"age\" key.");
        validateMapHasKey(employeeInput, "name", "The empoyee input map must have an \"name\" key.");
        validateMapHasKey(employeeInput, "salary", "The empoyee input map must have an \"salary\" key.");
    }

    private static void validateMapHasKey(Map<String, Object> map, String key, String message) {
        if (!map.containsKey(key)) {
            throw new HttpServerErrorException(BAD_REQUEST, "Invalid create request", message.getBytes(UTF_8), UTF_8);
        }
    }
}
