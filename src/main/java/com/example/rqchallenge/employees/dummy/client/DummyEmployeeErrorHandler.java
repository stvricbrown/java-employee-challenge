package com.example.rqchallenge.employees.dummy.client;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import com.example.rqchallenge.employees.dummy.exceptions.ServerBusyException;

public class DummyEmployeeErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        Series statusCodeSeries = httpResponse.getStatusCode().series();
        return statusCodeSeries == CLIENT_ERROR || statusCodeSeries == SERVER_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {

        HttpStatus statusCode = httpResponse.getStatusCode();
        if (statusCode == TOO_MANY_REQUESTS) {
            String message = format("The server reported \"%s: Too many requests.\"", statusCode.value());
            throw new ServerBusyException(message);
        }
    }
}