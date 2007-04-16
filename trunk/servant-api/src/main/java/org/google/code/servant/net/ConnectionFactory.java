// ConnectionFactory.java

package org.google.code.servant.net;

import java.io.IOException;

/**
 * This class represents a factory object for creation of specific
 * types of connections
 *
 * @version 1.1 08/09/2001
 * @author Alexander Shvets
*/
public interface ConnectionFactory {

  /**
   * Initializes the connection factory
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void init() throws IOException;

  /**
   * Cleans up the connection factory
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void cleanUp() throws IOException;

  /**
   * Creates the new connection
   *
   * @return  the new connection
   * @exception  IOException  if an I/O error occurs.
   */
  public Connection create() throws IOException;

}