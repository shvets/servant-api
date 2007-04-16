// ServantManager.java

package org.google.code.servant.net;

import java.io.IOException;

/**
 * This class describes a behavior of an object that can create/release
 * servants. They are created by the manager for the server each time
 * the client makes the request.
 *
 * Optimized version of this class could use pooling feature. By default
 * new servant object is created each time when client makes request.
 *
 * @author Alexander Shvets
 * @version 1.0 08/09/2001
 */
public class ServantManager {

  /** The factory object */
  private ServantFactory factory;

  /**
   * Creates new servant manager with the specified factory
   *
   * @param  factory  the servant factory
   */
  public ServantManager(ServantFactory factory) {
    this.factory = factory;
  }

  /**
   * Gets new servant
   *
   * @return  new servant
   */
  public Servant get() {
    return factory.create();
  }

  /**
   * Release used servant. After releasing this servant could be used
   * for executing other requests.
   *
   * @param  servant the servant to be released
   * @exception  IOException  if an I/O error occurs.
   */
  public void release(Servant servant) throws IOException {
    servant.release();
  }

}
