// StatefulServer.java

package org.google.code.servant.net;

import java.io.IOException;

/**
 * This interface describes the behavior of the server with states
 *
 * @version 1.0 05/21/2001
 * @author Alexander Shvets
 */
public interface Stateful {

  /**
   * Loads persistent information
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void load() throws IOException;

  /**
   * Saves persistent information
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void save() throws IOException;

}
