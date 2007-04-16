/*
 * @(#)EventGenerator.java 1.0 08/31/2000
 *
 */

package org.google.code.netapps.chat.basic;

import java.io.*;


import org.google.code.netapps.chat.event.*;
import org.google.code.servant.net.Interactor;
import org.google.code.servant.net.infoworm.InfoWorm;

/**
 * This class serving all answers that come from a server. Chat client
 * itself doesn't do it, client assigns this task to special object -
 * server reader.
 *
 * Server reader is realized as thread that reads information from
 * a server asynchronously. Information between the server and the client
 * is transferred in form of the packets. Some chat actions need more
 * than one packet. Reader collects that packets and forms chat action.
 *
 * @version 1.0 08/31/2000
 * @author Alexander Shvets
 */
public class EventGenerator implements Runnable {
  /** The thread */
  private transient Thread thread = new Thread(this);

  /* Is server reading process finished? */
  protected boolean done;

  /**
   * Support for producing some chat actions.
   */
  private ChatSupport support = new ChatSupport(this);

  /** The discontinuity of this thread */
  private int delay;

  /** The client for which reading of server output is preformed */
  private Interactor client;

  /**
   * Constructs server reader.
   */
  public EventGenerator(int delay, Interactor client) {
    this.delay  = delay;
    this.client = client;
  }

  /**
   * Start reading process
   */
  public void start() {
    thread.start();
  }

  /**
   * Stop reading process
   */
  public void stop() {
    done = true;
  }

  /** thread's life */
  public void run() {
    done = false;

    while(!done) {
      try {
        readAnswer();

        thread.sleep(delay);
      }
      catch(InterruptedException e1) {
        e1.printStackTrace();
        break;
      }
      catch(Exception e2) {
        e2.printStackTrace();
      }
    }
  }

  /**
   * Read response from server
   */
  private void readAnswer() throws IOException {
    while(client.existsResponse()) {
      InfoWorm response = (InfoWorm)client.response();

      String command = response.getFieldValue(Constants.COMMAND_FIELD);

      if(command.equalsIgnoreCase(Command.POLL)) {
        continue;
      }

      fireChatAction(response);
    }
  }

  /**
   * Methods that support generation of chat events.
  */

  /**
   * Add a ChatListener to the listener list.
   *
   * @param l  The ChatListener to be added
   */
  public synchronized void addChatListener(ChatListener l) {
    support.addChatListener(l);
  }

  /**
   * Remove a ChatListener from the listener list.
   *
   * @param l The ChatListener to be removed
   */
  public synchronized void removeChatListener(ChatListener l) {
    support.removeChatListener(l);
  }

  /**
   * Fire ChatEvent to registered listeners
   *
   * @param response the response
   */
  protected void fireChatAction(InfoWorm response) {
    support.fireChatAction(response);
  }

}
