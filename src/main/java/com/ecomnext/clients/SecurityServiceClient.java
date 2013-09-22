package com.ecomnext.clients;

import com.ecomnext.domain.TDataAccessException;
import com.ecomnext.domain.TNotFoundException;
import com.ecomnext.domain.TUser;
import com.ecomnext.domain.User;
import com.ecomnext.domain.exceptions.*;
import com.ecomnext.services.SecurityService;
import com.ecomnext.util.ListTransform;
import com.ecomnext.util.ScalaSupport;
import com.google.common.base.Function;
import com.twitter.finagle.Service;
import com.twitter.finagle.builder.ClientBuilder;
import com.twitter.finagle.stats.InMemoryStatsReceiver;
import com.twitter.finagle.thrift.ThriftClientFramedCodec;
import com.twitter.finagle.thrift.ThriftClientRequest;
import com.twitter.util.Duration;
import com.twitter.util.TimeoutException;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransportException;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Client for {@link SecurityService}.
 */
public class SecurityServiceClient {
    Service<ThriftClientRequest,byte[]> service;
    SecurityService.FinagledClient client;

    /**
     * @param connectionLimit maximum number of connections from the client to the host.
     */
    public SecurityServiceClient(String host, int port, int connectionLimit) {
        service = ClientBuilder.safeBuild(
                ClientBuilder.get()
                        .hosts(new InetSocketAddress(host, port))
                        .codec(ThriftClientFramedCodec.get())
                        .hostConnectionLimit(connectionLimit)
        );

        client = new SecurityService.FinagledClient(
                service,
                new TBinaryProtocol.Factory(),
                "MerchantsService",
                new InMemoryStatsReceiver());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                close();
            }
        });
    }

    public void close() {
        service.close();
    }

    public User getUser(String username, long timeout) {
        try {
            return new User(
                    client.getUser(username)
                            .apply(
                                    new Duration(TimeUnit.MILLISECONDS.toNanos(timeout))
                            )
            );
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            } else if (e instanceof TimeoutException) {
                throw new ServiceTimeoutException(e);
            } else if (e instanceof TApplicationException) {
                throw new ServiceRemoteException(((TApplicationException)e).getType(), e.getMessage());
            } else if (e instanceof TTransportException) {
                throw new ServiceTransportException(e);
            } else if (e instanceof TNotFoundException) {
                return null;
            } else if (e instanceof TDataAccessException) {
                throw new ServiceDataAccessException(e);
            } else {
                throw new ServiceException(e);
            }
        }
    }

    public List<User> getUsers(long timeout) {
        try {
            return ListTransform.transform(
                    ScalaSupport.toJavaList(
                            client.getUsers()
                                    .apply(
                                            new Duration(TimeUnit.MILLISECONDS.toNanos(timeout))
                                    )
                    ),
                    new Function<TUser, User>() {
                        @Override
                        public User apply(TUser input) {
                            return new User(input);
                        }
                    }
            );
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            } else if (e instanceof TimeoutException) {
                throw new ServiceTimeoutException(e);
            } else if (e instanceof TApplicationException) {
                throw new ServiceRemoteException(((TApplicationException)e).getType(), e.getMessage());
            } else if (e instanceof TTransportException) {
                throw new ServiceTransportException(e);
            } else if (e instanceof TNotFoundException) {
                throw new ServiceNotFoundException(e);
            } else if (e instanceof TDataAccessException) {
                throw new ServiceDataAccessException(e);
            } else {
                throw new ServiceException(e);
            }
        }
    }

    public int countUsersByRole(boolean enabled, List<String> roles, long timeout) {
        try {
            return (Integer) client.countUsersByRole(enabled, ScalaSupport.toScalaSeq(roles))
                            .apply(
                                    new Duration(TimeUnit.MILLISECONDS.toNanos(timeout))
                            );
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            } else if (e instanceof TimeoutException) {
                throw new ServiceTimeoutException(e);
            } else if (e instanceof TApplicationException) {
                throw new ServiceRemoteException(((TApplicationException)e).getType(), e.getMessage());
            } else if (e instanceof TTransportException) {
                throw new ServiceTransportException(e);
            } else if (e instanceof TNotFoundException) {
                throw new ServiceNotFoundException(e);
            } else if (e instanceof TDataAccessException) {
                throw new ServiceDataAccessException(e);
            } else {
                throw new ServiceException(e);
            }
        }
    }

    public void cleanUpOldUsers(long timeout) {
        try {
            client.cleanUpOldUsers().apply(new Duration(TimeUnit.MILLISECONDS.toNanos(timeout)));
        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw (ServiceException) e;
            } else if (e instanceof TimeoutException) {
                throw new ServiceTimeoutException(e);
            } else if (e instanceof TApplicationException) {
                throw new ServiceRemoteException(((TApplicationException)e).getType(), e.getMessage());
            } else if (e instanceof TTransportException) {
                throw new ServiceTransportException(e);
            } else if (e instanceof TNotFoundException) {
                throw new ServiceNotFoundException(e);
            } else if (e instanceof TDataAccessException) {
                throw new ServiceDataAccessException(e);
            } else {
                throw new ServiceException(e);
            }
        }
    }
}
