package com.ecomnext.domain.exceptions;

/**
 * Generic exception
 */
public class ServiceRemoteException extends ServiceException {

    protected int type;

    public ServiceRemoteException(int type, String s) {
        super(s);
        this.type = type;
    }

    public ServiceRemoteException(int type, String s, Throwable throwable) {
        super(s, throwable);
        this.type = type;
    }

    public ServiceRemoteException(int type, Throwable throwable) {
        super(throwable);
        this.type = type;
    }
}
