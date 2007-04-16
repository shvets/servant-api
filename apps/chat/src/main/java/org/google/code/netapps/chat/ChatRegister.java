/*
 * @(#)ChatRegister.java 1.0 07/07/99
 *
 */

package org.google.code.netapps.chat;

import org.google.code.netapps.chat.basic.*;
import org.google.code.netapps.chat.primitive.*;

/**
 * This provide an access to database of users, registered on server.
 *
 * @author Alexander Shvets
 * @version 1.0 07/07/99
 */
public class ChatRegister implements UserRegister {

  /*
   * Return true if user ("type", "name") can be registered on server
   * with passord "passord"
   *
   * @param   type   type of user
   * @param   name   name of user
   * @param   passord  passord
   */
  public boolean isRegistered(String login, String password) {
    if(login.startsWith(ParticipantType.CUSTOMER)) {
      return true;
    }

    return checkDataBase(login, password);
  }

  /* Simple paceholder for DB */
  private boolean checkDataBase(String login, String password) {
    if(password.equals("aaa"))
      return true;

    return false;
  }
}
