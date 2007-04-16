// TimeoutContextManager.java

package org.google.code.servant.net;

import java.util.Iterator;

/**
 * This class implements timeout context management. It goes through
 * the list of existing contexts and checks if the context is working
 * in timely manner.
 *
 * @version 1.0 08/12/2001
 * @author Alexander Shvets
 */
public class TimeoutContextManager extends DefaultContextManager
                                   implements Runnable {
  /** The thread */
  private transient Thread thread;

  /** Is main thread cycle finished? */
  private boolean done;

  /** The discontinuity of this thread */
  private final static int DELAY = 2000;

  /** The timeout */
  private int timeout;

  /**
   * Creates new context manager with specified timeout value
   *
   * @param timeout  the timeout value
   */
  public TimeoutContextManager(int timeout) {
    this.timeout = timeout;
  }

  /**
   * Starts the context manager a a thread
   */
  public void start() {
    done = false;

    if(thread == null) {
      thread = new Thread(this);
    }

    thread.start();
  }

  /**
   * Stops the context manager a a thread
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

  /** The thread's life */
  public void run() {
    while(!done) {
      Iterator iterator = contexts.values().iterator();

      while(iterator.hasNext()) {
        Context context = (Context)iterator.next();

        if(context.isLocked()) {
          continue;
        }

        long oldTime = context.getTouchTime();

        long newTime = System.currentTimeMillis();

        if((newTime - oldTime) >= timeout) {
          context.cancel();

          iterator.remove();
        }
      }

      try {
        thread.sleep(DELAY);
      }
      catch(InterruptedException e) {
        System.out.println(e);
      }
    }
  }

}
