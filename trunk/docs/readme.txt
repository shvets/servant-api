		NetLib library
		
		1. Basics

This library is useful for building arbitrary client-server applications.
Instead of specifying details of communication protocol, this library makes
accent on high level details of communication process. 

The communication between the client and the server is specified on general 
level. It allows to use this library for solving the most of the possible 
tasks, not losing in efficiency.

The library works with four main concepts: client, server, servant 
and connection. Client sends requests to a server. For each request 
the server creates special object - servant, that serves this request.
To have multithreading behavior and to handle all communication specific
details the connection object is used as wrapper around servant object.

There are three different managers for simplifynig the work with 
the server: servant, connection and context managers.

The servant manager allows TO keep under control allocation and 
deallocation of servants.

The connection manager simlifies usage of different transport layers.

The context manager is useful for building business servers with 
active users.

All classes for this library are located in org.javalobby.net and 
org.javalobby.util packages.

		2. The Client API

Client has three methods that should be implemented in derived classes: 

- void writeRequest(Object request) - writes the request to a server;

- Object readResponse() - reads the response from the server;

- Object[] pipe(Object request) - sends the request and receives 
the responses in one step. The AbstractClient class contains 
implementation of this logic.

- Logger getLogger() - gets the logger object;

- void setLogger(Logger logger) - sets the logger object.

The logger object is used for logging all messages that take place on the 
client (or server) side. It's present in org.javalobby.util package and 
has very simple interface:

- void logMessage(String message) - save a message-string into a logger system.

There is a simple implementation of the logger object in form of LogFile class.

Client doesn't have methods like connect()/disconnect(), because for
some types of clients these operations should be preformed invisibly.
Even more: connect-operation-disconnect sequence should be represented 
as atomic operation. The pipe() method is a good candidate for hiding 
such details.

		3. The Servant API

When the client establishes connection with the server, the last creates 
its representative - the servant object that  serves client's request.
After completing the service the servant is released (destroyed or came 
back to the pool, depending on the management policy). 

The servant has the following methods:

- Object readRequest(Object source) - reads the request from the source 
(supplemental to client's writeRequest() method);

- void writeResponse(Object response, Object destination) - writes 
the response to a destination (supplemental to client's readResponse() method);

- Object[] service(Object request) - performs the execution of client's 
request, preparing the responses;

- void service(Object source, Object destination) - reads the request from 
the source, performs the service by invoking previous method and then writes 
the responses into the destination. The AbstractServant class contains direct
implementation of this logic;

- void release() - releases the servant from the service. It's a callback 
method and informs the servant that it's out of the service and used 
resources could be released.

The source from where the servant is getting the request and the destination
to where the response should be written, are defined in the most general 
way - as Objects. This way we could plug-in arbitrary consumers-suppliers of 
the data.

Let's note that both client and servant can operate with the array of 
responses. This could be described as "one request - many responses" rule.
If the server implements complex business logic with multiple participants, 
involved into interactions, it is possible to have additional responses
on the server side. This rule will help to optimize processing for 
complex situations.

The ServantFactory interface helps us to build factories of servants. 
It has the only one method:

- Servant create() - creates new servant object.

Every new kind of server should have specific functionality that works 
with client's specific requests. This functionality is represented as 
an extension of basic Servant class. In turn, each server should also 
have specific ServantFactory class that returns extended servant object. 
For example, Web Server could have the following classes:

class WebServant extends AbstractServant {

  public Object service(Object request) throws IOException {
    // the code for serving specific client's requests
  }

  ...

}

class WebServantFactory extends ServantFactory {
  public Servant create() {
    return new WebServant();
  }
}

The delevoper doesn't work with the factory directly. Instead, the
ServantManager class is used. It simplifies creation and releasing 
of servants. The following API is used:

- Servant get() - get the new servant for serving new request. It could be 
completely new or reusable object.

- void release(Servant servant) - releases servant from manager's 
supervision.

The ServantManager class represents simple implementation, which creates
new servant for each client's request and removes servant object after 
completing the service. 

It is possible to have more efficient management algorithm. 
The PooledServantManager class represents the strategy with the pool support. 
At initialization time this manager creates predefined number of servant 
objects - a pool. If the server is asking for the next servant, the manager 
returns free servant from the pool. After completing the service the servant 
will be released and returned to a pool for reuse. In this case it's not 
necessarily to spend time for the object allocation during service time.

If the number of requests exceeds the common number of existing servants
in the pool, new servants will be created. But after completing the 
service they will be destroyed. It is required to keep the pool small 
enough and not to allow memory degradation, when new objects are created 
but not destroyed.

The PooledServantManager class has two constructors. First constructor 
creates default number of servants (16), whereas the second allows 
to specify the number of servants to be created:

- PooledServantManager(ServantFactory factory);

- PooledServantManager(ServantFactory factory, int poolSize).

		4. The Connection API

This class represents connection between the client and the server. Because
the server should handle arbirtary number of client's requests (we want to 
have parallel server implementation as basic), we'll consider the connection 
as wrapper object around the servant, allowing it to be executed as a thread.
This class also knows how to read request from and how to write the response 
to a client. 

The Connection interface has the following methods:

- Object getSource() - gets the source, from where to read the request;

- Object getDestination() - gets the destination, to where to write 
the response.

- void start() - starts the connection;

- void interrupt() - interrupt the connection (it is useful for 
long operations);

The AbstractConnection class is the convenient implementation of the 
Connection interface. The developer can use this class by adding 
implementation for two methods: getSource() and getDestination(). On this 
level of abstraction it is unknown who will play roles of data supplier 
and consumer.

The ConnectionFactory interface helps for building factories of connections.
It has the following methods:

- void init() - initializes factory;

- void cleanUp() - freed resources, occupied by factory;

- Connection create() - creates new connection object.

If the server wants to have specific implementation of communication
(transport) level, it should have specific connection and connection factory.
For example, Web Server could have the following classes:

class WebConnection extends AbstractConnection {

  public Object getSource() throws IOException {
    // return the source object, from where the request will be received
  } 

  public Object getDestination() throws IOException {
    // return the destination object, to where the response will be written
  }

}

class WebConnectionFactory extends ConnectionFactory {

  public void init() throws IOException {
    // initializes the factory
  }

  public void cleanUp() throws IOException {
    // cleans up the factory
  }
  
  public Connection create() {
    return new WebConnection(...);
  }
}

		5. Support for contexts 

Sometimes the server (especially business server) needs to support 
active users. To make this easy the following classes could be used.

The Context interface has the following methods:

- long getTouchTime() - gets the last touch time;

- void touch() - touches the context;

- boolean isLocked() - checks if context is locked;

- void setLocked(boolean locked) - Sets the locked boolean value;

- void cancel() - cancels the context.

Contexts will be controlled from special manager - context manager.

The servant's responsibility is to call touch() method each time,
when this context was involved into any action. Otherwise, context manager
after predefined period of time will remove this context from supervision
and communication between server and client will be cancelled.

The DefaultContext class is the default implementation of Context interface.

The ContextManager interface describes the behavior of context manager:

- void put(String name, Context context) - adds new context to management;

- Context get(String name) - gets the context by given name;

- void remove(String name) - removes the context from management;

- Map getContexts() - gets all contexts under management;

The DefaultContextManager class is the default implementation of 
ContextManager interface. It just keeps the list of contexts.

The following code could be used as basis for bulding the server with
contexts:

public class ContextualServer extends DefaultServer {

  public ContextualServer() {
    setContextManager(new DefaultContextManager());
  }

}

public class ContextualContext extends DefaultContext {
  private String info;

  public String getInfo() {
    return info;
  }

  public void setInfo(String info) {
    this.info = info;
  }

}

public class ContextualServant extends AbstractServant {
  private ContextManager contextManager;

  public ContextualServant() {
    contextManager = server.getContextManager();
  }

  public Object[] service(Object request) throws IOException {
    // register the user

    Context newContext = new PersonalContext();

    newContext.setInfo(...);

    contextManager.put(name, newContext);
  }
}

In this example we choose context management on the base of 
default inplementation. New context is created for supporting "info"
context value. Servant code contain a chunk of code that could be used 
for the registration.

The TimeoutContextManager class supports timeout feature. It means that
when context has last touch time out of predetermined interval, it'll
be removed. This class is implemented as thread and has 2 additional 
methods:

- void start() - starts the context manager;

- void stop() - stops the context manager.

		6. The Server API

The server has the following methods:

- void start() - starts the server;

- void stop() - stops the server;

- ConnectionManager getConnectionManager() - gets the connection manager;

- setConnectionManager(ConnectionManager connectionManager) - sets 
the connection manager;

- ServantManager getServantManager() - gets the servant manager for this 
server;

- void setServantManager(ServantManager servantManager) - sets up the servant
manager for this server;

- ContextManager getContextManager() - gets the context manager;

- setContextManager(ContextManager contextManager) - sets the context manager;

- Logger getLogger() - gets the logger object;

- void setLogger(Logger logger) - gets the logger object.

The DefaultServer class is the implementation of the Server interface. 
This class could be used as starting point for building arbitrary kind 
of server. The developer should set up appropriate strategy for managing 
servants (without or with a pool) and connections (direct or indirect). 
The context manager is optional and could be used only for servers with
active users. The typical scenario will look like:

  // 1. Creates default server
  DefaultServer server = new DefaultServer();

  // 2. Creates the connection factory, sets up specific parameters
  ConnectionFactory connectionFactory = new ...;

  // 3. Sets up connection factory
  server.setConnectionFactory(connectionFactory);

  // 4. Creates servant factory, sets up specific parameters, if necessary
  ServantFactory factory = new ...;

  // 5. Creates the servant manager with specified servant factory
  ServantManager servantManager = new PooledServantManager(factory);

  // 6. Applies servant management strategy
  server.setServantManager(servantManager);

  // 7. Creates context manager, if nescessary
  ContextManager contextManager = new ...;

  // 8. Applies context management strategy, if nescessary
  server.setContextManager(contextManager);

  After that the server colud be started and stopped:

  server.start();
  ...
  server.stop();

		7. Server modifications

In the simplest case server's responsibility is to start new servant for
each client's request. In more complex cases the server should perform
additional tasks. 

The Stateful interface should be implemented by the server with the state 
support: 

- void load() - loads the state;

- void save() - saves the state;

The DefaultServer behaves as Stateful object and could load/save its state 
during start/stop operations.

The Environmentable interface defines the ability to support environment in 
form of collection:

- Map getEnvironment() - gets the environment variables.

Server could share environment variables between servants. For example, 
if the server has variables, common for all users:

public class PersonalServer extends DefaultServer
                            implements Environmentable {
  public static String HOST_STRING = "host";
  public static String PORT_STRING = "port";

  protected Map environment = new HashMap();

  public PersonalServer() {
    environment.put(HOST_STRING, "localhost");
    environment.put(PORT_STRING, "8181");
  }

  public Hashtable getEnvironment() {
    return environment;
  }

}

public class PersonalServant extends AbstractServant {
  private Hashtable environment;

  public PersonalServant() {
    environment = server.getEnvironment();

    String hostName = (String)environment.get(PersonalServer.HOST_STRING);
    String port     = (String)environment.get(PersonalServer.PORT_STRING);

    System.out.println("Host name is  : " + hostName); // 'localhost'
    System.out.println("Port number is: " + port);     // '8181'
  }
}

In this example we have PersonalServer.HOST_STRING and 
PersonalServer.PORT_STRING variables, shared between all servants. 

Server could have additional configuration description in external file.
The Configurator class extends the Properties class and has additional 
methods:

public class ConfigurableServer extends DefaultServer {
  protected Configurator configurator;

  public ConfigurableServer(String fileName) {
    configurator = new Configurator(fileName);
  }

  public void load() throws IOException {
    configurator.load();
  }

  public void save() throws IOException {
    configurator.store();
  }

}

		8. Building interactive client

Sometimes it is enough for a client to send a request to a server side,
get response and finish execution. But there are clients, that could work
for a long period of time, sending requests and receiving responses in 
interactive mode. 

Such clients should use wrapper class Interactor. It has the following 
methods:

- void start() - starts the cycle of interactions;

- void stop() - stops the cycle of interactions;

- void request(Object request) - sends the request to a server in 
asynchronous mode;

- Object response() - gets the request from a server in asynchronous mode;

- boolean existsResponse() - checks whether response already exists or not;

- boolean isExit(Object request) - checks if the request is the command
for finishing interaction.

The Interactor class is a wrapper for any client. In such a way 
the separation between the interactive part of application and 
the transport layer takes place. It put all requests into tx-queue and 
all responses into rx-queue. This work will be done by separate thread in
asynchronous manner.

		9. Direct connection

To work with sockets on transport layer the classes from 
org.javalobby.net.direct package required. The DirectConnection handles
the way how to get the request from the source and how to put the response
to the destination. The DirectConnectionFactory handles initialization-
finalization requirements for server socket connection.

                10. Indirect connection 

In the simplest case we have connections that stay forever. It is acceptable
for direct socket connection. But we also have to handle another strategies 
of connection management.

The TimeoutConnectionManager represents the strategy with timeout support. 
During creation the developer should specify the timeout parameter. 
If connection doesn't perform any operation within this timeout interval, 
it will be removed from the list of connections and conversation between
the client and the server will be terminated. 

Such strategy is useful, for example, if the connection functions over 
HTTP protocol. In this case we don't have established connection all 
the time. Instead, we send packages from client to a server from time to time.

The activity of client is determined by the presense of any request on 
the server side. It could be regular request or special polling request. 
If client doesnt't send any command to a server side, at least polling
requests should be sent. To automate this process the Poller interface 
describes the behavior of the polling thread:

- void start() - starts the polling process;

- void stop() - stops the polling process;

- void poll() - sends single poll request;

- int getPollingTime() - gets the polling time.

                11. Connection over HTTP 

It is possible to work over HTTP protocol (see 
org.javalobby.net.infoworm package). HTTP requests and responses are 
represented by single InfoWorm class. There is no difference between 
request and response on class level. Each info-worm has the head as 
fields list and the body in form of bytes array. To get access to 
components of info-worm the following methods are used:

- List getHeader() - gets the header of info-worm;

- void setHeader(List header) - sets the header of info-worm;

- byte[] getBody() - gets the body of info-worm in form of bytes array;

- void setBody(byte[] body) - sets the body of info-worm;

- String getFieldValue(String key) - gets specified with the key field of 
header;

- void setField(String field) - sets new field to a header; the string 
should contain ":" delimiter;

- void setField(String key, String value) - sets new field to a header.

On client side the developer creates request and sends it through pipe()
to the server side. After completion execution on the server side
the responce could be retreived as set of strings and bytes array:

  InfoWorm request = new InfoWorm();

  request.setField("question1", "aaa?");
  request.setField("question2", "bbb?");

  byte body[] = FileUtil.getFileAsBytes(fileName);

  request.setField(InfoWorm.CONTENT_LENGTH_FIELD, String.valueOf(body.length));
  request.setBody(body);

  InfoWorm response = (InfoWorm)pipe(request);

  System.out.println(response.getFieldValue("answer")); // ccc!

  byte body[] = response.getBody();

Servant, in turn, could manipulate these objects in service() method:

  public Object[] service(Object requestObject) throws IOException {    
    InfoWorm request= (InfoWorm)requestObject;

    System.out.println(request.getFieldValue("question1")); // aaa?
    System.out.println(request.getFieldValue("question2")); // bbb?

    byte body[] = request.getBody();

    InfoWorm response = new InfoWorm();

    response.setField("answer", "ccc!");

    return response;
  }

To build complete web application with HTTP protocol in mind, two additional
classes are required. The InfoWormServant class handles how to work with
info-worms (i.e. with HTTP packets), whereas the InfoWormClient class is
used as base for building clients that communicate with such kind of 
servlet or with any other web server. 
