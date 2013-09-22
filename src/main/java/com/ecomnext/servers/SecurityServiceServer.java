package com.ecomnext.servers;

import com.ecomnext.services.SecurityService;
import com.ecomnext.services.SecurityServiceImpl;
import com.twitter.finagle.builder.Server;
import com.twitter.finagle.builder.ServerBuilder;
import com.twitter.finagle.thrift.ThriftServerFramedCodec;
import com.twitter.util.Duration;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Server for {@link SecurityService}.
 */
public class SecurityServiceServer {
    public static final String NAME = "MerchantsService";
    public static final int PORT = 7910;

    public static void main(String[] args) {
        SecurityService.FutureIface processor = new SecurityServiceImpl();

        final Server server = ServerBuilder.safeBuild(
                new SecurityService.FinagledService(processor, new TBinaryProtocol.Factory()),
                ServerBuilder.get()
                        .name(NAME)
                        .codec(ThriftServerFramedCodec.get())
                        .bindTo(new InetSocketAddress(PORT)));

        System.out.println(String.format("Server %s running", NAME));

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {

                System.out.println("Closing server " + NAME);

                if (server != null) {
                    server.close(new Duration(TimeUnit.SECONDS.toNanos(10)));
                }
            }
        });
    }
}
