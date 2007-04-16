/*
 * @(#)RoomNameGenerator.java 1.0 09/05/2000
 *
 */

package org.google.code.netapps.chat.basic;

import org.google.code.netapps.chat.primitive.*;

/**
 * Class for generating simple names for chat rooms.
 *
 * @version 1.0 09/05/2000
 * @author Alexander Shvets
*/
public class RoomNameGenerator extends NameGenerator {

  static final long serialVersionUID = -1425629531378892182L;

  /** The constant that specifies how many rooms can be created */
  public static final int MAX_ROOM_NUMBER = 128;

  /** The maximum number of rooms */
  protected int maxRoomNumber = MAX_ROOM_NUMBER;

  /** An array for remembering which rooms are used and which - free */
  private boolean[] memoryForNames = new boolean[MAX_ROOM_NUMBER];

  private int cnt = 0;

  /**
   * Creates name generator with spesified prefix
   */
  public RoomNameGenerator(String prefix) {
    super(prefix);
  }

  /**
   * Set up maximum number of rooms
   *
   * @param maxRoomNumber  the maximum number of rooms
   */
  public void setMaxRoomNumber(int maxRoomNumber) {
    this.maxRoomNumber = maxRoomNumber;

    memoryForNames = new boolean[maxRoomNumber];
  }

  /**
   * Get new room name
   *
   * @return  new room name
   */
  public String getNewName() {
    boolean isEmpty = true;
    for(int i=0; i < maxRoomNumber; i++) {
      if(memoryForNames[i] == true) {
        isEmpty = false;
        break;
      }
    }

    int index = -1;

    if(isEmpty) {
      cnt = 0;
      index = 0;
    }
    else {
      for(int checked=0; checked < maxRoomNumber; checked++, cnt++) {
        if(cnt == maxRoomNumber)
          cnt = 0;
  
        if(memoryForNames[cnt] == false) {
          index = cnt++;
          break;
        }
      }

      if(index == -1) {
        return null;
      }
    }

    memoryForNames[index] = true;

    return prefix + Integer.toString(index+1);
  }

  /**
   * Release specified name to be reused
   *
   * @param name  the name that can be reused after release
   */
  public void release(String name) {
    String numberStr = name.substring(prefix.length());
    try {
      int number = Integer.parseInt(numberStr);
      memoryForNames[number-1] = false;
    }
    catch(NumberFormatException e) {
      System.out.println("Name " + name + " is wrong for this generator.");
    }
  }

}
