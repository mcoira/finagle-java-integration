<a name="Top"></a>
How many times did you started a project and the Hello World example did not help you. We want to provide a better example which can be really useful to get started with Finagle and Java.

For the impatient:
	<a href="#Quick Start">Quick Start</a>

## An ecomnext.com architecture overview

At ecomnext.com we are building a marketing and business intelligence SaaS product that will analyze millions of tracking events and merchants catalogs in order to provide useful information to our clients.

We have previous experience in Big Data companies so we have deal with the problem of Web Scale. Data is always growing and we need to provide scalable systems. The good news is we aren't alone in the fight and there are many amazing tools to help us dealing with that.

We also know that every company and every software will change to adapt themselves to the present.

Having all that in mind we have to build the best architecture we can and here we show some clues that worked for us:

    * Asynchrony is good
    * Services based infrastructure is flexible, scalable and helps to reuse code.
    * SQL is amazing sometimes, NoSQL is amazing other times, don't be afraid of mixing them.

### So, what technologies are under the hood of ecomnext.com?

Our first technical decision was the programing language. We love the JVM and we are proficiency in Java, so we chose it as main language. We also like Python and other languages which could be very convenient for some functionalities so we didn't want to prevent their use. That make us select Thrift as scalable cross-language services development framework.

Having chosen the main things, let's go for the rest of it. We are going to show the system from top to bottom.

At the top of the architecture we have two entry points.
* The API. This module is used to track users behaviour and it can be used by our customers to retrieve data from our systems. The API has to be blazing fast, stateless and easy to integrate (a simple REST service). That requirements pointed us directly to <a href="http://netty.io/">Netty</a> based solutions and <a href="http://www.playframework.com/">playframework 2</a> was the perfect fit. We also can avoid to install a Servlet container like Tomcat or Jetty (although we need a reverse proxy like <a href="http://nginx.com/">Ngix</a>).
* The dashboard. This web will be used by our customers to config the system and is a direct communication channel between them and us. The amount of users concurrently is very low and we had very good experience in similar projects with Spring MVC that's why we chose it.

There are some programs that run in the background, for example the one that process API tracking events or some others that perform tasks based on events or scheduled planifications. The events processor is a standard Java program with intensive threading use. In the future it is possible that we move it to a simpler Actors based model (<a href="http://akka.io/">akka</a> could be the one). The tasks are build on Spring Batch framework.

All those modules from above require to operate with services that are common for them and they are Thrift based.

In the bottom we have the data sources:
* PostgreSQL to store configurations and customer accounts.
* <a href="http://redis.io/">Redis</a> to store event queues, system stats and caches.
* <a href="https://cassandra.apache.org/">Cassandra</a> to store behavioral information. We love Cassandra and it is a key tool in our system so we will provide further info.

<a name="Quick Start"></a>

## Quick Start

### Migrating from Apache Thrift to Finagle Thrift

We had our system working with Apache Thrift and we thought that the change will be clean but NO.

Structs defined in Thrift IDL files are compiled into different classes with Apache and Finagle compilers, we mean that they have different methods so they aren't interchangeable. Apache classes has a constructor and getter/setter methods, Finagle classes don't.

To create an object of one Apache class:

	MyClass obj = new MyClass();
	obj.setAttr1("Hello");

To create an object of one Finagle class:

	MyClass$.MODULE$.apply("Hello");

This is a pain but what it is really painfully is getting the attributes of the object. We build many objects but we use much more 'get' methods.

To get an attribute in Apache class:

	obj.getAttr1();

To get an attribute in Finagle class:

	obj.attr1();

Another annoying thing is Finagle use scala.collections classes for list, set and map instead of java.util classes. That messes with our pure Java code.

Therefore we needed to migrate progressively and avoid to mix Scala and Java collections in our previous code. The solution was straightforward, wrapper classes.

We build wrapper classes that always have the same definition. They also have a toTObject() method that returns the Thrift object and a method to build a wrapper object from the Thrift object. So when we had all references changed from Apache classes to our wrapper classes we were able to change from Apache to Finagle. We only need to change builder and toTObject methods. This way we separate our code from the serialization project selected. If we want to change from Apache Thrift to another Thrift compiler or even to avro we have little work to do. In our project we made those wrappers immutables but for this example we won't do it in order to make it cleaner.

### Maven configuration

We need to add scrooge-maven-plugin to use <a href="https://github.com/twitter/scrooge">Scrooge</a> (a custom Thrift compiler) that generates Scala code for Thrift IDL definitions and maven-scala-plugin to compile those scala files into bytecodes.

We have to add some dependencies:
* libthrift
* scrooge-core: necessary to compile thrift definitions into scala code.
* finagle-thrift: necessary if we use --finagle flag in scrooge-maven-plugin.
* scala-library: we use it for scala/java integration. It must be the same version used by scrooge.

### Java - Scala integration

Finagle classes use scala.collections.Seq, scala.collection.Map and scala.collection.Set instead of java.util.List, java.util.Map and java.util.Set so we need to translate them. In this example we provide a ScalaSupport class that illustrate all those transformations.

### Wrappers

If we want to keep separate your code from the Thrift implementation and don't mix Scala code with our Java we need to write a wrapper for every class defined in thrift files.

#### Exceptions

We have created some wrapper exceptions that extend RuntimeException. There are two different reasons. On one hand, we keep separated Thrift code from our code, on the other hand we don't need to treat all possible exceptions in all levels.

### Services

To create a Finagle Thrift service, you must implement the `FutureIface` interface that <a href="https://github.com/twitter/scrooge">Scrooge</a> generates for your service. So far so good but there are some Scala particularites to keep in mind.

#### The ghost method for collections parameters in services classes

After implementing the four methods defined in SecurityServices.thrift we compile and see the following error:

	com.ecomnext.services.SecurityServiceImpl is not abstract and does not override abstract method countUsersByRole$default$2() in com.ecomnext.services.SecurityService.FutureIface
	public class SecurityServiceImpl implements SecurityService.FutureIface {
    	   ^

What the hell is countUsersByRole$default$2()?

The quick response is that Scala supports default arguments. Arguments which are optional, and when they are not provided then a default expression is computed by a synthetic method.

If you want to delve into the matter check this out: <a href="http://docs.scala-lang.org/sips/completed/named-and-default-arguments.html">Default Arguments in Scala</a>

Now we are going to show how it affects to our code. 

If we check SecurityService.scala we can see the definition of the FutureIface interface:

	trait FutureIface {
    
	    def getUser(username: String): Future[com.ecomnext.domain.TUser]
	    
	    def getUsers(): Future[Seq[com.ecomnext.domain.TUser]]
	    
	    def countUsersByRole(enabled: Boolean, roles: Seq[String] = Seq[String]()): Future[Int]
	    
	    def cleanUpOldUsers(): Future[Unit]
	}

If we look carefully to countUsersByRole method we can see how roles implement default argument. Now we use Jad decompiler to see the Java code in SecurityService$FutureIface.class:
	
	public static interface SecurityService$FutureIface
	{

	    public abstract Future getUser(String s);

	    public abstract Future getUsers();

	    public abstract Future countUsersByRole(boolean flag, Seq seq);

	    public abstract Seq countUsersByRole$default$2();

	    public abstract Future cleanUpOldUsers();
	}

There it is, the ghost method. This is the moment to see how is implemented the synthetic method so we decompile SecurityService$FutureIface$class.class:

	public static abstract class SecurityService$FutureIface$class
	{

	    public static Seq countUsersByRole$default$2(SecurityService.FutureIface $this)
	    {
	        return (Seq)Seq$.MODULE$.apply(Nil$.MODULE$);
	    }

	    public static void $init$(SecurityService.FutureIface futureiface)
	    {
	    }
	}

Now you can fully understand what it is and where it came from.

#### SNotFoundException

As we can see in <a href="http://thrift.apache.org/docs/features/">Thrift features</a> null cannot be returned directly from a function so we throw an exception instead. That exception will be treated by the client and we can return a null value there.

#### Exceptions

As you can see in <a href="https://github.com/twitter/finagle/blob/master/README.md#Implementing%20a%20Pool%20for%20Blocking%20Operations%20in%20Java">Finagle documentation</a> the way to perform blocking operations is using a com.twitter.util.Function0 but in case we need to throw an exception from inside of a blocking operation we use a com.twitter.util.ExceptionalFunction0 instead. The difference between them is that we change apply() for applyE().

And don't forget the common way to throw an exception in a non-blocking operation:
	
	return Future.exception()

#### Combine futures

There are nothing easier, we use join() and it returns a Tuple with all the results. If we want to join them into a single result we can use transformedBy().

The method SecurityServiceImpl.getUsers() demonstrates it.

#### void return

We can't return a Future of void so we return a Future<BoxedUnit> instead, and that means that our return statement is:

	return BoxedUnit.UNIT;

### Clients

According to the decisions of keeping separate Scala and Java code and also Thrift implementation from your own code, we provide the client classes.

#### Scala - Java integration

Values retrieved from Finagle services are Scala based and we want Java code instead so we use ScalaSupport class again in order to make the transformations and we get the wrappers for the Finagle objects too.

#### Exceptions

We translate Finagle exceptions into our own RuntimeExceptions.

#### Close service

We need to provide a close() method to close the service or our programs will never end. We also add a shutdownHook to close connections before exit, if somebody kills the process.
