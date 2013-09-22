package com.ecomnext.domain.exceptions;

/**
 * Encapsulate thrift transport exceptions.
 */
public class ServiceTransportException extends ServiceException {
    public ServiceTransportException(String s) {
        super(s);
    }

    public ServiceTransportException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ServiceTransportException(Throwable throwable) {
        super(throwable);
    }
}
