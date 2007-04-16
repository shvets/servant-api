// Interactor.java

package org.google.code.servant.net;

import java.io.IOException;

import org.google.code.servant.util.SyncQueue;

/**
 * Behavior for a client that sends requests and gets responses
 * in asynchronous way.

 * This class represents a client implementation that sends requests
 * and gets responses in asynchronous way.
 *
 * @version 1.1 08/14/2001
 * @author Alexander Shvets
 */
public abstract class Interactor implements Runnable {

  /** The thread */
  private transient Thread thread;

  /** Is main thread cycle finished? */
  private boolean done;

  /**
   * The buffer for accumulating user input in form of commands for a server.
   */
  protected SyncQueue txQueue = new SyncQueue();

  /**
   * The buffer for accumulating responses from a server.
   */
  protected SyncQueue rxQueue = new SyncQueue();

  /** The client object */
  protected Client client;

  /**
   * Creates new interactive client-wrapper
   *
   * @param client  the client object
   */
  public Interactor(Client client) {
    this.client = client;
  }

  /**
   * Starts the interaction cycle
   */
  public void start() {
    done = false;

    if(thread == null) {
      thread = new Thread(this);
    }

    thread.start();
  }

  /**
   * Stops the interaction cycle
   */
  public void stop() {
    done = true;

    try {
      thread.join();
    }
    catch(InterruptedException e) {
      System.out.println(e);
    }

    thread = null;
  }

  /**
   * Sends the request from the client
   *
   * @param request the request from the client
   * @exception  IOException  if an I/O error occurs.
   */
  public void request(Object request) throws IOException {
    txQueue.add(request);
  }

  /**
   * Gets the response from the server
   *
   * @return   the response from the server
   * @exception  IOException  if an I/O error occurs
   */
  public Object response() throws IOException {
    return rxQueue.getAndRemove();
  }

  /**
   * Checks if response from a server exists at this time
   *
   * @return  true if response from a server exists at this time
   */
  public boolean existsResponse() {
    return rxQueue.size() > 0;
  }

  /**
   * Checks if request contains a command that will stop the interaction
   *
   * @param  request  the request from a user
   * @return true if the request contains a command that will stop
   *         the interaction; false otherwise
   */
  public abstract boolean isExit(Object request);

  /* Thred's life */
  public void run() {
    while(!done) {
      try {
        // get request from a user from tx buffer
        Object request = txQueue.getAndRemove();

        // send request to a server and read response
        Object[] responses = client.pipe(request);

        // put responses to a rx buffer
        for(int i=0; i < responses.length; i++) {
          rxQueue.add(responses[i]);
        }

        if(isExit(request)) {
          break;
        }
      }
      catch(IOException e) {
//        e.printStackTrace();
        client.getLogger().logMessage(e.getMessage());
        break;
      }
      catch(Throwable e) {
//        e.printStackTrace();
        client.getLogger().logMessage(e.getMessage());
        break;
      }
    }
  }

}
