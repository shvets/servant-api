// Connection.java

package org.google.code.servant.net;

import java.io.IOException;

/**
 * This class represents connection object that could read request,
 * start serving through servant manipulation  and write response as
 * a thread.
 *
 * @version 1.0 08/07/2001
 * @author Alexander Shvets
 */
public interface Connection extends Runnable {

  /**
   * Gets the source object from where the connection will read the request
   *
   * @return  the source object from where the connection will read the request
   * @exception  IOException  if an I/O error occurs.
   */
  public Object getSource() throws IOException;

  /**
   * Gets the destination object, to where the connection will write
   * the response
   *
   * @return  the destination object from where the connection will
   *          read the request
   * @exception  IOException  if an I/O error occurs.
   */
  public Object getDestination() throws IOException;

  /**
   * Starts the connection
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void start() throws IOException;

  /**
   * Cancels the connection
   *
   */
  public void cancel();

}
