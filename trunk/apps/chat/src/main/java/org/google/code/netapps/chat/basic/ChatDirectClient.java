// ChatClient.java

package org.google.code.netapps.chat.basic;

import java.io.*;
import java.util.*;

import org.google.code.servant.net.infoworm.InfoWormClient;
import org.google.code.servant.net.infoworm.InfoWorm;

public class ChatDirectClient extends InfoWormClient {

  /** name of the user */
  protected String name;

  /** password for the user */
  protected String password;

  /**
   * Creates new InfoWorm client with the predefined host name 
   * and port number.
   */
  public ChatDirectClient() {
    super();
  }

  /**
   * Creates new InfoWorm client.
   *
   * @param host  the host name
   * @param port  the port number
   */
  public ChatDirectClient(String  host, int port) {
    super(host, port);
  }

  /**
   * Sets request from a user.
   *
   * @param request object-request from a user.
   * @exception  IOException  if an I/O error occurs.
   */
  public void writeRequest(Object request) throws IOException {
    InfoWorm infoWorm = new InfoWorm();

    String line = request.toString();
    
    StringTokenizer st = new StringTokenizer(line);

    if(st.hasMoreTokens()) {
      String command = st.nextToken();
      infoWorm.setField(Constants.COMMAND_FIELD, command);

      if(st.hasMoreTokens()) {
        infoWorm.setField(Constants.MESSAGE_FIELD, 
                          line.substring(line.indexOf(command) + command.length()).trim());
      }
    }
    else {
      infoWorm.setField(Constants.MESSAGE_FIELD, "");
    }

    infoWorm.setField(Constants.USER_NAME_FIELD, name);
    infoWorm.setField(Constants.PASSWORD_FIELD, password);

    out.writeInfoWorm(infoWorm);
  }

  /**
   * Try to register an user with specified name and passord
   *
   * @param  name  the name of user
   * @param  password  the password of user
   * @return  true if registration stage finished successfully.
   */
  public void register(String name, String password) throws RegistrationException {
    this.name    = name;
    this.password = password;

    InfoWorm response = null;

    try {
      response = (InfoWorm)pipe(Command.REGISTER)[0];
    }
    catch(IOException e) {
//      e.printStackTrace();
      throw new RegistrationException(e.getMessage());
    }

    String status = response.getFieldValue(Constants.STATUS_FIELD);

    boolean isOk = new Boolean(status).booleanValue();

    if(!isOk) {
      throw new RegistrationException(new String(response.getBody()).trim());
    }
    
    String message = response.getFieldValue(Constants.USER_NAME_FIELD);

    if(message.length() > 0) {
      this.name = message;
    }
  }

}