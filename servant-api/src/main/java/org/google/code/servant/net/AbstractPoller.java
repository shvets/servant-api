// AbstractPoller.java

package org.google.code.servant.net;

import java.io.IOException;

/**
 *
 * @version 1.0 08/15/2001
 * @author Alexander Shvets
 */
public abstract class AbstractPoller implements Poller {
  /** the thread */
  private transient Thread thread = new Thread(this);

  /* Is polling process finished? */
  private boolean done;

  /** Client that is served with this poller */
  protected Client client;

  /** The polling time */
  private int pollingTime;

  /**
   * Creates abstract poller object
   *
   * @param client  the client which is used this poller
   * @param pollingTime  the polling time
   */
  public AbstractPoller(Client client, int pollingTime) {
    this.client      = client;
    this.pollingTime = pollingTime;
  }

  /**
   * Starts the polling process
   */
  public void start() {
    thread.start();
  }

  /**
   * Stops the polling process
   */
  public void stop() {
    done = true;
  }

  /**
   * Sends poll command
   */
  public abstract void poll() throws IOException;

  /**
   * Gets the polling time value
   *
   * @return the polling time value
   */
  public int getPollingTime() {
    return pollingTime;
  }

  /**
   * The thread's life
   */
  public void run() {
    done = false;

    while(!done) {
      try {
        poll();

        thread.sleep(pollingTime);
      }
      catch(InterruptedException e) {
        e.printStackTrace();
        client.getLogger().logMessage(e.getMessage());
        break;
      }
      catch(Exception e) {
        e.printStackTrace();
        client.getLogger().logMessage(e.getMessage());
      }
    }
  }

}
