package com.ecomnext.domain.exceptions;

/**
 * Service timeout exception may happen when a service is called
 * and it does not respond in the time we have given to it to respond.
 */
public class ServiceTimeoutException extends ServiceException {
    public ServiceTimeoutException(String s) {
        super(s);
    }

    public ServiceTimeoutException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServiceTimeoutException(Throwable throwable) {
        super(throwable);
    }
}
