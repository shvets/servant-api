/*
 * @(#)SyncQueue.java 1.1 09/06/2000
 *
 */

package org.google.code.servant.util;

import java.util.List;
import java.util.ArrayList;

/**
 * Helper class for working with the shared buffer in threaded environment.
 * It solves a well known task of communication between Consumer and Producer.
 * These actors are considered as threads that want to receive accress to
 * shared recource. If Consumer asks about data, but they doesn't yet
 * received, it sleeps. Producer put data to buffer, awaking Consumer's dreaming.
 * But if data is already presented inside a buffer and it doesn't yet readed by
 * Consumer, it sleeps etc.
 *
 * @version 1.1 09/06/2000
 * @author Alexander Shvets
 */
public class SyncQueue {

  /** This buffer contains shared data */
  private List buffer = new ArrayList();

  /*
   * Get data from a shared buffer.
   *
   * @return   the first element in shared buffer
  */
  public synchronized Object getAndRemove() {
    if(buffer.size() == 0) {
      try {
        wait();
      }
      catch(InterruptedException e) {}
    }

    notify();

    Object o = buffer.get(0);

    buffer.remove(o);

    return o;
  }

  /*
   * Add object to a shared buffer.
   *
   * param s  the future contentobject that will be added to a shared buffer
  */
  public synchronized void add(Object o) {
    buffer.add(o);

    notify();
  }

  /*
   * Add data to a shared buffer.
   *
   * param s  The array of objects that will be added to a shared buffer
  */
  public synchronized void add(Object[] os) {
    for(int i=0; i < os.length; i++) {
      buffer.add(os[i]);
    }

    notify();
  }

  /*
   * Get size of shared buffer.
   *
   * return  size of shared buffer
  */
  public int size() {
    return buffer.size();
  }

  /**
   * Chechk if buffer already contain this object
   *
   * @param o   an object for that this check will performed.
   * @return true if queue contains the object; false otherwise
   */
  public boolean contains(Object o) {
    return buffer.contains(o);
  }

}
