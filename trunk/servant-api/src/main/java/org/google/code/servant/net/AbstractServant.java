// AbstractServant.java

package org.google.code.servant.net;

import java.io.IOException;

/**
 * This class contains implementation of the "service" method.
 *
 * @version 1.1 08/09/2001
 * @author Alexander Shvets
 */
public abstract class AbstractServant implements Servant {
  /** The server object */
  protected Server server;

  /**
   * Creates new servant
   *
   * @param server  the server
   */
  public AbstractServant(Server server) {
    this.server = server;
  }

  /**
   * Reads the request from the client source, executes it
   * (see service(Object request) method) and writes received
   * responses array into client destination.
   *
   * @param source  the client source
   * @param destination  the client destination
   * @exception  IOException  if an I/O error occurs.
   */
  public void service(Object source, Object destination) throws IOException {
    Object request = readRequest(source);

    Object[] responses = service(request);

    for(int i=0; i < responses.length; i++) {
      writeResponse(responses[i], destination);
    }
  }

  /**
   * Releases all captured resources.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void release() throws IOException {}

}
