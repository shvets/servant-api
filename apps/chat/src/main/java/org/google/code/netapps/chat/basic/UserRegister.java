/*
 * @(#)UserRegister.java 1.0 08/31/2000
 *
 */

package org.google.code.netapps.chat.basic;

/**
 * This interface represents simple API to database for registered
 * on server users. It revises if specified user can receive an access
 * to a server.
 *
 * @author Alexander Shvets
 * @version 1.0 08/31/2000
 */
public interface UserRegister {

  /**
   * Return true if user ("type", "name") is registered on the server
   * with passord "passord"
   *
   * @param   login   login
   * @param   password  passord
   */
  public boolean isRegistered(String login, String password);

}
