package com.example.rqchallenge.employees.dummy.client;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.example.rqchallenge.employees.dummy.exceptions.EmployeeNotFoundException;
import com.example.rqchallenge.employees.dummy.exceptions.ServerBusyException;
import com.example.rqchallenge.employees.dummy.exceptions.ServerErrorException;

public class DummyEmployeeErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        Series statusCodeSeries = httpResponse.getStatusCode().series();
        return statusCodeSeries == CLIENT_ERROR || statusCodeSeries == SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {

        HttpStatus statusCode = httpResponse.getStatusCode();
        Series errorSeries = statusCode.series();
        if (errorSeries == SERVER_ERROR) {
            String message= format("The server reported \"%s: The server reported an error.\"", statusCode.value());
            throw new ServerErrorException(message);
        } else if (errorSeries == CLIENT_ERROR) {
            if (statusCode == NOT_FOUND) {
                String message= format("The server reported \"%s: The employee was not found.\"", statusCode.value());
                throw new EmployeeNotFoundException(message);
            }

            if (statusCode == TOO_MANY_REQUESTS) {
                String message= format("The server reported \"%s: Too many requests.\"", statusCode.value());
                throw new ServerBusyException(message);
            }
        }
    }
}