/*
 * @(#)ChatEvent.java 1.1 08/30/2000
 *
 */

package org.google.code.netapps.chat.event;

import org.google.code.netapps.chat.basic.*;
import org.google.code.servant.net.infoworm.InfoWorm;


/**
 * The chat event. It is generated when some chat action occurs.
 *
 * @version 1.1 08/30/2000
 * @author Alexander Shvets
*/
public class ChatEvent extends java.util.EventObject {

  /** The command that initiates this event */
  private InfoWorm response;

  /**
   * Constructs a ChatEvent object with the specified source object.
   *
   * @param source      the object where the event originated
   */
  public ChatEvent(Object source, InfoWorm response) {
    super(source);

    this.response = response;
  }

  /**
   * Get the command
   *
   * @returns the command string
   */
  public String getCommand() {
    return response.getFieldValue(Constants.COMMAND_FIELD);
  }

  /**
   * Get the room name
   *
   * @return  the chat room name, for which this event is addressed
   */
  public String getRoomName() {
    return response.getFieldValue(Constants.ROOM_NAME_FIELD);
  }

  /**
   * Get the message
   *
   * @return  the message
   */
  public String getMessage() {
    return response.getFieldValue(Constants.MESSAGE_FIELD);
  }

  /**
   * Get the body
   *
   * @return  the body
   */
  public byte[] getBody() {
    return response.getBody();
  }

}
