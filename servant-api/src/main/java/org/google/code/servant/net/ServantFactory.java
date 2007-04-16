// ServantFactory.java

package org.google.code.servant.net;

/**
 * This class represents a factory object for creation of specific
 * types of servants
 *
 * @author Alexander Shvets
 * @version 1.0 03/27/2001
 */
public interface ServantFactory {

  /**
   * Creates new servant object
   *
   * @return  new servant object
   */
  public Servant create();

}
