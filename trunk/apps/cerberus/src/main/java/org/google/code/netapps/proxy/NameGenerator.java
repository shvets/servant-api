// NameGenerator.java

package org.google.code.netapps.proxy;

import java.io.Serializable;

/**
 * This class could generate random names
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class NameGenerator implements Serializable {

  private static char alphabets[] = {
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
      'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
      'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4',
      '5', '6', '7', '8', '9', '0'
  };

  /**
   * Discables creation of instances of this class
   */
  private NameGenerator() {}

  /**
   * Gets new random name
   *
   * @return new random name
   */
  public static String newName() {
    char file_char[] = new char[10];
    for(int i = 0; i < 10; i++) {
      int location = (int)(Math.random() * 1000D) % alphabets.length;
      file_char[i] = alphabets[location];
    }

    return new String(file_char) + ".cache";
  }

}
