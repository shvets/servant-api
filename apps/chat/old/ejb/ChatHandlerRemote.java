/**
 * Automatically generated remote interface for com.kana.realtime.ejb.ChatHandlerBean class.
 */
package org.google.code.netapps.chat.chat.ejb;

import java.rmi.*;

import javax.ejb.*;

/**
 * Remote interface.
 */
public interface ChatHandlerRemote extends EJBObject {

  /**
   * 
   */
  public StringBuffer handleRequest(String[] request)
         throws RemoteException;
}
