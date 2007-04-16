// PooledServantManager.java

package org.google.code.servant.net;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * This is an implementation of servant manager's behavior which
 * uses the pool of servants.
 *
 * Servants pool has maximum size MAX_NUMBER_OF_SERVANTS, but if the
 * number of requests is more than this number, manager creates
 * additional servlets. They won't be reused, just will be removed
 * from the memory after completing execution.
 *
 * @author Alexander Shvets
 * @version 1.0 03/27/2001
 */
public class PooledServantManager extends ServantManager {

  /** The Maximum size of servants pool */
  private final static int MAX_NUMBER_OF_SERVANTS = 16;

  /** The servants pool */
  private List servants = new ArrayList();

  /** Current number of servants that perform requests */
  private int numberOfServants = 0;

  /** the size of pool */
  private int poolSize;

  /**
   * Creates servant manager with servants pool
   *
   * @param factory  the servant factory
   */
  public PooledServantManager(ServantFactory factory) {
    this(factory, MAX_NUMBER_OF_SERVANTS);
  }

  /**
   * Creates servant manager with servants pool
   *
   * @param factory  the servant factory
   * @param poolSize  the size of pool
   */
  public PooledServantManager(ServantFactory factory, int poolSize) {
    super(factory);

    this.poolSize = poolSize;

    for(int i = 0; i < poolSize; i++) {
      servants.add(super.get());
      numberOfServants++;
    }
  }

  /**
   * Gets new servant
   *
   * @return  new servant
   */
  public Servant get() {
    Servant servant = null;

    synchronized(servants) {
      if(servants.isEmpty()) {

        servant = super.get();

        numberOfServants++;
      }
      else {
        servant = (Servant)servants.get(0);
        servants.remove(0);
      }
    }

    return servant;
  }

  /**
   * Release used servant. After releasing this servant could be used
   * for executing other requests.
   *
   * @param  servant the servant to be released
   * @exception  IOException  if an I/O error occurs.
   */
  public void release(Servant servant) throws IOException {
    super.release(servant);

    synchronized(servants) {
      if(servants.size() >= poolSize) {
        numberOfServants--;
      }
      else {
        servants.add(servant);
      }
    }
  }

  /**
   * Performs some final actions before this manager will be destroyed.
   *
   * @throws Throwable the <code>Exception</code> raised by this method
   */
  public void finalize() throws Throwable {
    servants.clear();

    numberOfServants = 0;
  }

}
