// AbstractConnection.java

package org.google.code.servant.net;

import java.io.IOException;

/**
 * This is incomlete implementation of Connection interface.
 * Derived classes should implement getSource() and getDestination()
 * methods to work with required transport layer.
 *
 * @version 1.0 08/07/2001
 * @author Alexander Shvets
 */
public abstract class AbstractConnection implements Connection {
  /** The thread object */
  private transient Thread thread = new Thread(this);

  /** The server object */
  private Server server;

  /**
   * Creates new abstract connection.
   *
   * @param server  the server object
   */
  public AbstractConnection(Server server) {
    this.server = server;
  }

  /**
   * Starts the connection
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void start() throws IOException {
    thread.start();
  }

  /**
   * Cancels the connection
   *
   */
  public void cancel() {
    thread.interrupt();
  }

  /**
   * Performs service in threading fashion
   */
  public void run() {
    try {
      ServantManager servantManager = server.getServantManager();

      Servant servant = servantManager.get();

      servant.service(getSource(), getDestination());

      servantManager.release(servant);
    }
    catch (IOException e) {
      server.getLogger().logMessage(e.toString());
    }
  }

}
