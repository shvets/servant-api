//Logger.java

package org.google.code.servant.util;

import java.io.Serializable;

/**
 * This interface specifies commen behavior for objects that can save
 * log events
 *
 * @version 1.0 03/27/2001
 * @author Alexander Shvets
 */
public interface Logger extends Serializable {

  /**
   * Writes an information to a log file
   *
   * @param message a line that'll be added to the end of
   * the log file
   */
  public void logMessage(String message);

}
