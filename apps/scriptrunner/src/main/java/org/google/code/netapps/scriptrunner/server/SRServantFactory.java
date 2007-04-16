// SRServantFactory.java

package org.google.code.netapps.scriptrunner.server;

import org.google.code.servant.net.Servant;
import org.google.code.servant.net.ServantFactory;

/**
 * This class could create servant objects, specific for our server
 * (ScriptRunner server).
 *
 * @version 1.0 05/16/2001
 * @author Alexander Shvets
 */
public class SRServantFactory implements ServantFactory {

  /** The ScriptRunner server */
  private SRServer server;

  /**
   * Creates new class-factory for specified server.
   *
   * @param server  the ScriptRunner server 
   */
  public SRServantFactory(SRServer server) {
    this.server = server;
  }
  
  /**
   * Creates new servant object of required type
   *
   * @return  new servant object
   */
  public Servant create() {
    return new SRServant(server);
  }

}
