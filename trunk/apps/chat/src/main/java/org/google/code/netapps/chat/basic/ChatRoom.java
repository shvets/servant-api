/*
 * @(#)ChatRoom.java 1.01 07/01/99
 *
 */

package org.google.code.netapps.chat.basic;

import java.util.*;

import org.google.code.netapps.chat.primitive.*;

/**
 * The class that represent chat room as a container for participants
 * of chat conversation. The main difference comparing with parent class
 * NameableList consists in ability to hold objects with different types,
 * derived from common Nameable interface.
 *
 * Chat room implements Nameable interface, as its components. So,
 * programmer can creates "room inside room".
 *
 * Room has 2 properties - name of room and state of customer request.
 *
 * @version 1.01 07/01/99
 * @author Alexander Shvets
 */
public class ChatRoom extends NameableList implements Nameable {
  static final long serialVersionUID = 6153691325669016060L;

  /** flags that represents visibility mode of chat room */
  final public static String SUPERVISOR_IS_VISIBLE_FOR_ALL = "11"; // CSR,  customer
  final public static String SUPERVISOR_IS_VISIBLE_FOR_CSR = "10"; // CSR,  !customer
  final public static String SUPERVISOR_IS_NOT_VISIBLE     = "00"; // !CSR, !customer

  /** Is the limit for number of characters for chat room reached? */
  private boolean limitReached;

  /** The state of chat room - one constant from RequestState interface */
  private String state;

  /** The visibility mode for chat room */
  private String visibilityMode;

  /** The interaction which holds transcript and other properties for current
      chat conversation */ 
  private Interaction interaction;

  /** The name of chat room */
  private String name;

  /**
   * Constructs a chat room with the specified name and initial
   * "pending" state.
   *
   * @param   name   the name of chat room
   */
  public ChatRoom(String name) {
    super(Participant.class);

    this.name  = name;
    this.state = RequestState.PENDING_STATE;

    this.visibilityMode = SUPERVISOR_IS_NOT_VISIBLE;
    limitReached = false;
  }

  /**
   * Adds the specified component to the end of this list.
   * If user try to add object with a type, different from type, that
   * suppors this list, a runtime exception will occur.
   *
   * @param   nameable   the component to be added.
   * @exception  IllegalArgumentException if an user try to add object with
   *             illegal type
   */
  public void add(Nameable nameable) {
    if(!clazz.isInstance(nameable))
      throw new IllegalArgumentException(
            "Component with type " +
            "\"" + nameable.getClass().getName() + "\"" +
            " cannot be added to container that support only type derived from " +
            "\"" + clazz.getName() + "\"");

    if(!names.contains(nameable))
      names.addElement(nameable);
  }

  /**
   * Get name of chat room.
   *
   * @return  name of chat room
   */
  public String getName() {
    return name;
  }

  /**
   * Get qualified name of chat room.
   *
   * @return  qualified name of chat room
   */
  public String getQualifiedName() {
    String mode = "";
    if(visibilityMode.equals(SUPERVISOR_IS_VISIBLE_FOR_ALL))
      mode = "supervisor is visible for all";
    else if(visibilityMode.equals(SUPERVISOR_IS_VISIBLE_FOR_CSR))
      mode = "supervisor is visible for csr";
    else if(visibilityMode.equals(SUPERVISOR_IS_NOT_VISIBLE))
      mode = "supervisor is not visible";

    return name + " <" + mode + ">";
  }

  /**
   * Get state.
   *
   * @return  state
   */
  public String getState() {
    return state;
  }

  /**
   * Set status.
   *
   * @param  state state to set up
   */
  public boolean setState(String state) {
    if(state.equals(RequestState.PENDING_STATE)   ||
       state.equals(RequestState.OPEN_STATE)      ||
       state.equals(RequestState.ACCEPTED_STATE)  ||
       state.equals(RequestState.ESCALATED_STATE) ||
       state.equals(RequestState.HOLD_STATE)      ||
       state.equals(RequestState.COMPLETED_STATE) ||
       state.equals(RequestState.EXPIRED_STATE)   ||
       state.equals(RequestState.INACTIVATED_STATE)) {

      this.state = state;
      return true;
    }

    return false;
  }

  /**
   * Get interaction.
   *
   * @return  the interaction
   */
  public Interaction getInteraction() {
    return interaction;
  }

  /**
   * Set interaction.
   *
   * @param  interaction  the interaction for this chat room
   */
  public void setInteraction(Interaction interaction) {
    this.interaction = interaction;
  }

  /**
   * Get state of visibility mode.
   *
   * @return  the state of visibility mode
   */
  public String getVisibilityMode() {
    return visibilityMode;
  }

  /**
   * Set state of visibility mode.
   *
   * @param  visibilityMode  the state of visibility mode
   */
  public void setVisibilityMode(String visibilityMode) {
    this.visibilityMode = visibilityMode;
  }

  /**
   * Set a boolean value that specifies the limit for number of characters for chat room
   *
   * @param  limitReached  the boolean value that specifies the limit for number of
   *                       characters for chat room 
   */
  public void setLimitReached(boolean limitReached) {
    this.limitReached = limitReached;
  }

  /**
   * Check if the limit for number of characters for chat room is reached
   *
   * @return  true if the limit for number of characters for chat room is reached
   */
  public boolean isLimitReached() {
    return limitReached;
  }

  /**
   * Get qualified names of all of objects in this container.
   *
   * @return qualified names of all of objects in this container
   */
  public String[] getQualifiedNames() {
    int sz = names.size();
    String[] result = new String[sz];
    for(int i=0; i < sz; ++i) {
      Nameable n = (Nameable)names.elementAt(i);
      result[i] = n.getQualifiedName();
    }

    return result;
  }

  /**
   * Compares two Objects for equality. Two rooms will be equals
   *          if they both have the same name.
   *
   * @param   object  the reference object with which to compare.
   * @return  true if this object is the same as the obj
   *          argument; false otherwise.
   */
  public boolean equals(Object object) {
    if(object instanceof String) {
      String name = (String)object;
      return this.name.equals(name);
    }
    else if(object instanceof ChatRoom) {
      ChatRoom chatRoom = (ChatRoom)object;
      return this.name.equals(chatRoom.name);
    }

    return false;
  }

  /**
   * Clones the chat room object
   *
   * @return  a clone 
   */
  public Object clone() {
    ChatRoom newRoom = new ChatRoom(name);

    newRoom.names          = (Vector)this.names.clone();
    newRoom.state          = this.state;
    newRoom.visibilityMode = this.visibilityMode;
    newRoom.interaction    = this.interaction;
  
    return newRoom;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return  a string representation of the object.
   */
  public String toString() {
    return getQualifiedName();
  }


  public static void main(String[] args) {
    Customer c1 = new Customer("cust1");

    CSR csr1 = new CSR("csr1");
    CSR csr2 = new CSR("csr2");

    Expert e1 = new Expert("exp1");

    Supervisor s1 = new Supervisor("su1");

    ChatRoom room = new ChatRoom("Hell");

    room.add(c1);
    room.add(csr1);
    room.add(csr2);
    room.add(e1);
    room.add(s1);

    System.out.println("Chat room with 5 participants:");
    room.printList();

    room.remove(e1);
    System.out.println("List without expert:");
    room.printList();

    room.remove(s1);
    System.out.println("List without supervisor:");
    room.printList();

    ChatRoom clonedRoom = (ChatRoom)room.clone();
    System.out.println("\nA room cloned from room <room>");
    clonedRoom.printList();

    ChatRoom room2 = new ChatRoom("Inferno");

    System.out.println("room2 " + room2);

    System.out.println("Changing state for room2 to HOLD_STATE");
    room2.setState(RequestState.HOLD_STATE);

    System.out.println("room2 " + room2);

  }

}


