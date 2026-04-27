package com.CS7319.Group06.eventual.exception;

/**
 * DaoException - custom exception for dao errors.
 */
public class DaoException extends RuntimeException {

    public DaoException() {
        super();
    }

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Exception cause) {
        super(message, cause);
    }
}
