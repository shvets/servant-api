/*
 * @(#)ChatServantFactoryjava 1.0 09/13/2000
 *
 */

package org.google.code.netapps.chat.basic;

import org.google.code.servant.net.ServantFactory;
import org.google.code.servant.net.Servant;

/**
 * This class represents a factory object for creation servants
 * for serving chat conversation
 *
 * @version 1.0 09/13/2000
 * @author Alexander Shvets
 */
public class ChatServantFactory implements ServantFactory {

  /** The stateful server  */
  private ChatServer server;

  /**
   * Creates a factory
   *
   * @param server  stateful server
   */
  public ChatServantFactory(ChatServer server) {
    this.server = server;
  }

  /**
   * Creates servant
   *
   * @return  servant object
   */
  public Servant create() {
    return new ChatServant(server);
  }

}

