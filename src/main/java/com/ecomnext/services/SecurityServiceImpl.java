package com.ecomnext.services;

import com.ecomnext.domain.*;
import com.ecomnext.util.ScalaSupport;
import com.twitter.util.*;
import org.apache.thrift.TApplicationException;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.Nil$;
import scala.runtime.BoxedUnit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
* Asynchronous implementation of {@link SecurityService.FutureIface}.
*/
public class SecurityServiceImpl implements SecurityService.FutureIface {
    // In Scala, one can call directly to the FuturePool, but Java gets confused
    // between the object and class, so it's best to instantiate an ExecutorServiceFuturePool directly
    ExecutorService pool = Executors.newFixedThreadPool(4); // Number of threads to devote to blocking requests
    ExecutorServiceFuturePool futurePool = new ExecutorServiceFuturePool(pool); // Pool to process blocking requests so server thread doesn't

    /**
     * This class encapsulate a blocking operation to retrieve a user. A real use case
     * can be retrieve it from a data base or a cache.
     */
    public static class BlockingUserRetriever extends ExceptionalFunction0<User> {
        private String username;
        private boolean useRandom;
        private Random random = new Random();

        public BlockingUserRetriever(String username, boolean useRandom) {
            this.username = username;
            this.useRandom = useRandom;
        }

        @Override
        public User applyE() throws Throwable {
            User user = null;

            try {
                // Implement your blocking operation here

                List<String> roles = new ArrayList<String>(2);
                roles.add("ROLE_ADMIN");
                roles.add("ROLE_USER");
                User tmp = new User().setUsername(username).setPassword("1234. Really?")
                        .setEnabled(true).setRoles(roles).setUserType(UserType.EMPLOYEE);

                // only 50% of requests exist, the rest throw an exception
                if (useRandom && (random.nextInt(2) + 1) % 2 == 0) {
                    user = tmp;
                } else if (!useRandom) {
                    user = tmp;
                }

            } catch (Exception e) {
                // An SQLException is not thrown but tries to illustrate how can we treat this kind of errors
                if (e instanceof SQLException) {
                    throw new TDataAccessException.Immutable(e.getMessage());
                }
                // Other exceptions like RuntimeException can be handled too
                else {
                    throw new TApplicationException(TApplicationException.INTERNAL_ERROR, e.getMessage());
                }
            }

            // null cannot be returned directly from a thrift function so we throw an exception
            if (user == null)
                throw new TNotFoundException.Immutable("");
            else
                return user;
        }
    }

    /**
     * Makes a blocking operation can throw exceptions and transforms the
     * result before returning it.
     */
    @Override
    public Future<TUser> getUser(String username) {
        return futurePool.apply(new BlockingUserRetriever(username, true))
                // we add a transformation to change the User into a TUser
                .transformedBy(new FutureTransformer<User, TUser>() {
                    @Override
                    public TUser map(User value) {
                        return value.toTObject();
                    }
                });
    }



    @Override
    public Future<Seq<TUser>> getUsers() {
        // retrieve multiple users in different calls and finally join them
        // at the end we transform the Java List into a Scala Seq.
        return futurePool.apply(new BlockingUserRetriever("user1", false))
                .join(futurePool.apply(new BlockingUserRetriever("user2", false)))
                .transformedBy(
                        new FutureTransformer<Tuple2<User, User>, List<TUser>>() {
                            @Override
                            public List<TUser> map(Tuple2<User, User> value) {
                                List<TUser> list = new ArrayList<TUser>(2);
                                list.add(value._1().toTObject());
                                list.add(value._2().toTObject());
                                return list;
                            }

                            // if we call BlockingUserRetriever with useRandom=true then
                            // one of the users of maybe both can fail because don't exist so
                            // we catch the exception and return an empty list instead
                            @Override
                            public List<TUser> handle(Throwable throwable) {
                                return new ArrayList<TUser>();
                            }
                        }
                ).transformedBy(
                        new FutureTransformer<List<TUser>, Seq<TUser>>() {
                            @Override
                            public Seq<TUser> map(List<TUser> value) {
                                return ScalaSupport.toScalaSeq(value);
                            }
                        }
                );
    }

    @Override
    public Future<Object> countUsersByRole(boolean enabled, Seq<String> roles) {
        return Future.value((Object) Integer.valueOf(2));
    }

    /**
     *
     */
    public Seq countUsersByRole$default$2() {
        return (Seq) Seq$.MODULE$.apply(Nil$.MODULE$);
    }

    public static class BlockingCleaner extends ExceptionalFunction0<BoxedUnit> {
        @Override
        public BoxedUnit applyE() throws Throwable {
            return BoxedUnit.UNIT;
        }
    }


    @Override
    public Future<BoxedUnit> cleanUpOldUsers() {
        return futurePool.apply(new BlockingCleaner());
    }
}
