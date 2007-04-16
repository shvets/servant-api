/**
 * Automatically generated home interface for com.kana.realtime.ejb.ChatHandlerBean class.
 */
package org.google.code.netapps.chat.chat.ejb;

import java.rmi.*;

import javax.ejb.*;

/**
 * Home interface.
 */
public interface ChatHandlerHome extends EJBHome {

  final static String JNDI_NAME = "ejb/ChatHandler";

  /**
   * 
   */
  public ChatHandlerRemote create()
         throws RemoteException, CreateException;
}
