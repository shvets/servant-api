// Poller.java

package org.google.code.servant.net;

import java.io.IOException;

/**
 * This interface describes poller behavior
 *
 * @version 1.0 05/21/2001
 * @author Alexander Shvets
 */
public interface Poller extends Runnable {

  /**
   * Starts the polling process
   */
  public void start();

  /**
   * Stops the polling process
   */
  public void stop();

  /**
   * Sends poll command
   */
  public void poll() throws IOException;

  /**
   * Gets the polling time value
   *
   * @return the polling time value
   */
  public int getPollingTime();

}
