// RegistrationException.java

package org.google.code.netapps.chat.basic;

/**
 * This exception is intended for registration exceptional situations.
 *
 * @version 1.1 08/09/2001
 * @author Alexander Shvets
 */
public class RegistrationException extends Exception {

  /**
   * Creates new exception for registration process
   *
   * @param message  the message
   */
  public RegistrationException(String message) {
    super(message);
  }

}
