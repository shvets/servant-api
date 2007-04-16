// Client.java

package org.google.code.servant.net;

import java.io.IOException;
import java.io.Serializable;

import org.google.code.servant.util.Logger;

/**
 * The most common behavior for a client.
 *
 * @version 1.1 08/09/2001
 * @author Alexander Shvets
 */
public interface Client extends Serializable {

  /**
   * Writes the request from the client to the server.
   *
   * @param request the request from the client.
   * @exception  IOException  if an I/O error occurs.
   */
  public void writeRequest(Object request) throws IOException;

  /**
   * Reads the response from the server.
   *
   * @return   the response from the server.
   * @exception  IOException  if an I/O error occurs.
   */
  public Object readResponse() throws IOException;

  /**
   * Performs "pipe" operation (writeRequest/readResponse as atomic action)
   *
   * @param request  the request from the client
   * @return  the responses array from the server
   */
  public Object[] pipe(Object request) throws IOException;

  /**
   * Gets the logger object
   *
   * @return  the logger object
   */
  public Logger getLogger();

  /**
   * Sets the logger object
   *
   * @param logger  the logger object
   */
  public void setLogger(Logger logger);

}

