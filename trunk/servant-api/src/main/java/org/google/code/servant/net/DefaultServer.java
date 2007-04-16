// DefaultServer.java

package org.google.code.servant.net;

import java.io.IOException;
import java.io.InterruptedIOException;

import org.google.code.servant.util.Logger;

/**
 * The default implementation of the Server interface.
 *
 * @version 1.1 08/09/2001
 * @author Alexander Shvets
 */
public class DefaultServer implements Stateful, Server, Runnable {
  /** the thread object */
  private Thread thread = new Thread(this);

  /** Is server finished a work? */
  protected boolean done;

  /** The connection manager */
  protected ConnectionFactory connectionFactory;

  /** The manager that can create/release servant objects */
  protected ServantManager servantManager;

  /** The manager that can work with users contexts */
  protected ContextManager contextManager;

  /** The logger object */
  protected Logger logger = new Logger() {
    public void logMessage(String message) {
      System.out.println(message);
    }
  };

  /**
   * Starts the server
   *
   * @exception  Exception if exception occurs.
   */
  public void start() throws Exception {
    connectionFactory.init();

    thread.start();

    done = false;

    load();
  }

  /**
   * Stops the server
   *
   * @exception  Exception if exception occurs.
   */
  public void stop() throws Exception {
    done = true;

    connectionFactory.cleanUp();

    save();
  }

  /**
   * Loads persistent information
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void load() throws IOException {}

  /**
   * Saves persistent information
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void save() throws IOException {}

  /**
   * Performs some final actions before this manager will be destroyed.
   *
   * @throws Throwable the <code>Exception</code> raised by this method
   */
  public void finalize() throws Throwable {
    if(!done) {
      stop();
    }
  }

  /**
   * Gets the connection factory
   *
   * @return the connection factory
   */
  public ConnectionFactory getConnectionFactory() {
    return connectionFactory;
  }

  /**
   * Sets the connection factory
   *
   * @param connectionFactory the connection factory
   */
  public void setConnectionFactory(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  /**
   * Gets the object that is the manager for servants
   *
   * @param servantManager the object that is the manager for servants
   */
  public void setServantManager(ServantManager servantManager) {
    this.servantManager = servantManager;
  }

  /**
   * Sets the manager for servants
   *
   * @return the manager for servants
   */
  public ServantManager getServantManager() {
    return servantManager;
  }

  /**
   * Gets the context manager
   *
   * @return the context manager
   */
  public ContextManager getContextManager() {
    return contextManager;
  }

  /**
   * Sets the context manager
   *
   * @param contextManager the context manager
   */
  public void setContextManager(ContextManager contextManager) {
    this.contextManager = contextManager;
  }

  /**
   * Gets the logger object
   *
   * @return  the logger object
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Sets the logger object
   *
   * @param logger  the logger object
   */
  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  /**
   * Thread's life
   */
  public void run() {
    while(!done) {
      try {
        Connection connection = connectionFactory.create();

        connection.start();

        // added for sencitivity for interrupt() method
        Thread.currentThread().sleep(100);
      }
      catch(InterruptedException e1) {
        // go out from a infinity cycle when interrupt() method is called
        break;
      }
      catch(InterruptedIOException e2) {
        // processing soTimeout value
        continue;
      }
      catch (IOException e3) {
        logger.logMessage(e3.toString());
        break;
      }
    }
  }

}
