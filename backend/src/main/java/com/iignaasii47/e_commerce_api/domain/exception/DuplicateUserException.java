package com.iignaasii47.e_commerce_api.domain.exception;

public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException(String message) {
        super(message);
    }

}
