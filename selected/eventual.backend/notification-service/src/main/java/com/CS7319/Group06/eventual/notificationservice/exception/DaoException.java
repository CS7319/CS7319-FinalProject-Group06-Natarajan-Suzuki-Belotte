package com.CS7319.Group06.eventual.notificationservice.exception;

/**
 * DaoException - custom exception for dao errors.
 */
public class DaoException extends RuntimeException {
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
