/*
 * @(#)ChatSupport.java 1.0 08/30/2000
 *
 */

package org.google.code.netapps.chat.event;

import org.google.code.servant.net.infoworm.InfoWorm;

import java.util.*;

/**
 * This is an utility class that can be used by classes that support 
 * chat events listening.
 *
 * @version 1.0 08/30/2000
 * @author Alexander Shvets
 */
public class ChatSupport {

  /** The list of chat listeners */
  private Vector listeners = new Vector();

  /** The source for chat event */
  private Object source;

  /**
   * Constructs an ChatSupport object with the specified source object.
   *
   * @param source the object that plays a role of generator of chat
   * events.
   */
  public ChatSupport(Object source) {
    this.source = source;
  }

  /**
   * Add a ChatListener to the listeners list.
   *
   * @param l The ChatListener to be added
   */
  public synchronized void addChatListener(ChatListener l) {
    listeners.addElement(l);
  }

  /**
   * Remove a ChatListener from the listener list.
   *
   * @param l The ChatListener to be removed
   */
  public synchronized void removeChatListener(ChatListener l) {
    listeners.removeElement(l);
  }

  /**
   * Report all registered listeners when a chat event occured.
   *
   */
  public void fireChatAction(InfoWorm response) {
    Vector targets = null;

    // Cloning of listeners list to eliminate race condition
    synchronized (this) {
      targets = (Vector)listeners.clone();
    }

    // Creating an event
    ChatEvent evt = new ChatEvent(source, response);

    // Notification of all registered listeners
    for (int i = 0; i < targets.size(); i++) {
      ChatListener target = (ChatListener)targets.elementAt(i);
      target.chatPerformed(evt);
    }
  }

}
