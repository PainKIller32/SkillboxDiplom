package main.presentation.exception;

import java.util.HashMap;

public class FalseResultException extends RuntimeException {
    private HashMap<String, String> errors;

    public FalseResultException() {
    }

    public FalseResultException(HashMap<String, String> errors) {
        this.errors = errors;
    }

    public HashMap<String, String> getErrors() {
        return errors;
    }
}