package com.ecomnext.domain.exceptions;

/**
 * CData access exceptions may happen when there is an error in any of the datasources.
 * For example a connection with any of the datasources can be closed.
 */
public class ServiceDataAccessException extends ServiceException {
    public ServiceDataAccessException() {
        super();
    }

    public ServiceDataAccessException(String s) {
        super(s);
    }

    public ServiceDataAccessException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServiceDataAccessException(Throwable throwable) {
        super(throwable);
    }
}
