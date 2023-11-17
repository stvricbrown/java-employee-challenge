package com.example.rqchallenge.employees.dummy.exceptions;

public class ServerBusyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServerBusyException(String message) {
        super(message);
    }

}
