/*
 * @(#)Participant.java 1.1 08/30/00
 *
 */

package org.google.code.netapps.chat.primitive;

/**
 * Basic class for all participants of chat conversation.
 * All participants should have some name. Participant cannot change its
 * own name.
 *
 * @version 1.1 08/30/00
 * @author Alexander Shvets
 */
public class Participant implements Nameable {

  /** Last touched time - for timeout checking mechanizm */
//  private long lastTime;

  /** Is participant blocked - timeout checking isn't performed for this one */
//  private boolean isBlocked;

  /** The name of participant */
  private String name;

  /**
   * Constructs a chat participant with the specified name.
   *
   * @param   name   the name of chat participant
   */
  public Participant(String name) {
    this.name = name;

//    setLastTime(System.currentTimeMillis());
//    isBlocked = false;
  }

  /**
   * Get unique name of participant.
   *
   * @return  unique name of participant
   */
  public String getName() {
    return name + "$" + hashCode();
  }

  /**
   * Get alias (name, used in conversation) of participant.
   *
   * @return  alias of participant
   */
  public String getAlias() {
    return name;
  }

  /**
   * Get type of participant.
   *
   * @return  type of participant
   */
  public String getType() {
    return "participant";
  }

  /**
   * Get qualified name of participant.
   *
   * @return  qualified name of participant
   */
  public String getQualifiedName() {
    return getType() + " " + getName();
  }

  /**
   * Compares two Objects for equality. Two participants will be equals
   *          if they both have the same name.
   *
   * @param   object  the reference object with which to compare.
   * @return  true if this object is the same as the obj
   *          argument; false otherwise.
   */
  public boolean equals(Object object) {
    if(object instanceof String) {
      String name = (String)object;

      return this.getName().equals(name);
    }
    else if(object instanceof Participant) {
      Participant participant = (Participant)object;
      if(this.getClass().equals(participant.getClass()))
        return this.getName().equals(participant.getName());
    }

    return false;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return  a string representation of the object.
   */
  public String toString() {
    return getType() + " " + getAlias();
  }

  // These methods support timeout-watching system

  /**
   * Get a time when this participant was touched by timeout-watching system.
   *
   * @return last touching time
   */
/*  public long getLastTime() {
    return lastTime;
  }
*/
  /**
   * Set a last touching time for this participant.
   *
   * @param  lastTime last touching time
   */
/*  public void setLastTime(long lastTime) {
    this.lastTime = lastTime;
  }
*/
  /**
   * Block this participant (timeout-watching system shouldn't take
   * into account this participant).
   *
   * @param  isBlocked  flag that specifies if this participant
   *                    should be blocked
   */
/*  public void setBlocked(boolean isBlocked) {
    this.isBlocked = isBlocked;
  }
*/
  /**
   * Check if this participant is blocked.
   *
   * @return  true if participant is blocked
   */
/*  public boolean isBlocked() {
    return isBlocked;
  }
*/
  public static void main(String[] args) {
    Participant p1 = new Participant("alex");
    Participant p2 = new Participant("nick");
    Participant p3 = new Participant("igor");
    Participant p4 = new Participant("igor");

    System.out.println("Participant p1 : " + p1);
    System.out.println("Participant p2 : " + p2);
    System.out.println("Participant p3 : " + p3);

    System.out.println("Participant p2 equals p3 : " + p2.equals(p3));

    System.out.println("Participant p3 equals \"igor\" : " + p3.equals("igor"));
    System.out.println("Participant p3 equals \"igor+$+hashCode()\" : " + p3.equals("igor$"+ p3.hashCode()));

    System.out.println("Participant p4 equals \"igor\" : " + p4.equals("igor"));
    System.out.println("Participant p4 equals \"igor+$+hashCode()\" : " + p4.equals("igor$"+ p4.hashCode()));

    System.out.println("Participant p4 equals \"igor+$+hashCode()\" : " + p4.equals("igor$"+ p3.hashCode()));

    System.out.println("Participant p3 equals p3 : " + p3.equals(p3));

    System.out.println("Participant p4 equals p3 : " + p4.equals(p3));
  }

}