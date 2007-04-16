/*
 * @(#)ChatListener.java 1.0 08/30/2000
 *
 */

package org.google.code.netapps.chat.event;

/* 
 * The listener interface for listening chat events. 
 *
 * @version 1.0 08/30/2000
 * @author Alexander Shvets
 */
public interface ChatListener extends java.util.EventListener {

  /**
   * Invoked when a chat event occurs.
   */
  public void chatPerformed(ChatEvent event);

}
