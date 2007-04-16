// Servant.java

package org.google.code.servant.net;

import java.io.IOException;

/**
 * This interface defines a behavior of server representative.
 * It will be created for the server for each client connection with the help
 * of ServantFactory class.
 *
 * @version 1.1 08/09/2001
 * @author Alexander Shvets
 */
public interface Servant {

  /**
   * Reads request from user's source
   *
   * @param source the source from which servant can read client's request
   * @return   client's request
   * @exception  IOException  if an I/O error occurs.
   */
  public Object readRequest(Object source) throws IOException;

  /**
   * Writes response to client's destination
   *
   * @param response  prepared response
   * @param destination the destination object
   * @exception  IOException  if an I/O error occurs.
   */
  public void writeResponse(Object response, Object destination) throws IOException;

  /**
   * Performs the "service" routine: for ech client's request the servant
   * should prepare responses array
   *
   * @param request  the request from the client
   * @return  the responses list
   * @exception  IOException  if an I/O error occurs.
   */
  public Object[] service(Object request) throws IOException;

  /**
   * Reads the request from the client source, executes it
   * (see service(Object request) method) and writes received
   * responses array into client destination.
   *
   * @param source  the client source
   * @param destination  the client destination
   * @exception  IOException  if an I/O error occurs.
   */
  public void service(Object source, Object destination) throws IOException;

  /**
   * Releases all captured resources.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void release() throws IOException;

}
