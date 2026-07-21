package com.iignaasii47.e_commerce_api.domain.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException(String message) {
        super(message);
    }

}
