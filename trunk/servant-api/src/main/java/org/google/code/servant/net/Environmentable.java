// Environmentable.java

package org.google.code.servant.net;

import java.util.Map;

/**
 * This interface describes the behavior of the server with environment
 *
 * @version 1.0 09/12/2001
 * @author Alexander Shvets
 */
public interface Environmentable {

  /**
   * Gets the evironment
   *
   * @return he evironment
   */
  public Map getEnvironment();

}
