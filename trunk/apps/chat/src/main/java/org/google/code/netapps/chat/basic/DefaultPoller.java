/*
 * @(#)DefaultPoller.java 1.0 08/30/2000
 *
 */

package org.google.code.netapps.chat.basic;

import java.io.*;

import org.google.code.servant.net.AbstractPoller;
import org.google.code.servant.net.Client;

/**
 *
 * @version 1.0 08/30/2000
 * @author Alexander Shvets
 */
public class DefaultPoller extends AbstractPoller {
  /**
   * Creates default poller object
   *
   * @param client  the client which is used this poller
   * @param pollingTime  the polling time
   */
  public DefaultPoller(Client client, int pollingTime) {
    super(client, pollingTime);
  }

  /**
   * This method perform single poll
   */
  public void poll() throws IOException {
    client.pipe(Command.POLL);
  }

}
