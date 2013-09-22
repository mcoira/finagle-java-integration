package com.ecomnext.domain.exceptions;

/**
 * Not found exceptions may happen when a requested object is not
 * found by the remote service.
 */
public class ServiceNotFoundException extends ServiceException {
    public ServiceNotFoundException() {
        super();
    }

    public ServiceNotFoundException(String s) {
        super(s);
    }

    public ServiceNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServiceNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
