/*
 * @(#)ChatServer.java 1.0 09/05/2000
 *
 */

package org.google.code.netapps.chat.basic;

import java.util.*;
import java.io.*;

import org.google.code.netapps.chat.primitive.*;
import org.google.code.servant.net.DefaultServer;
import org.google.code.servant.net.Environmentable;
import org.google.code.servant.util.Configurator;

/**
 * This class contains basic functionality for chat server. It specifies how
 * to read from properties file, but doesn't know how to perform timeout
 * watching behavior
 *
 * @version 1.0 09/05/2000
 * @author Alexander Shvets
 */
public abstract class ChatServer extends DefaultServer
  implements /*Registrable, */Environmentable {
  /** This constants specifies the property "maximum number of chat rooms"*/
  private final static String MAX_ROOM_NUMBER_PROPERTY = "max.room.number";

//  protected ContextManager contextManager;

  /** The environment that holds all contexts for registered users */
  protected Map environment = new HashMap();

  /** The configurator object */
  protected Configurator configurator;

  /** Is debug mode on? */
  protected boolean isDebug = false;

  /** The name of file that holds the state of chat server */
  private String stateFileName;

  /**
   * Creates chat server object
   *
   * @param logFileName the name of log file
   * @param stateFileName the name of file that holds the state of chat server
   * @param props the file with properties for this chat server
   */
  public ChatServer(String configFileName, String stateFileName) throws IOException {
//    environment.put(ChatServant.CONTEXTS_PROPERTY, new Hashtable());

    configurator = new Configurator(configFileName);

    configurator.load();

    this.stateFileName = stateFileName;
  }

/*  public ContextManager getContextManager() {
    return contextManager;
  }

  public void setContextManager(ContextManager contextManager) {
    this.contextManager = contextManager;
  }
*/

  /**
   * Get the evironment for this object
   *
   * @return he evironment for this object
   */
  public Map getEnvironment() {
    return environment;
  }

/*  public Context register(String name, String password) 
              throws RegistrationException {
    return new ChatContext(name, password);
  }
*/
  /**
   * Get the configurator
   *
   * @return the configurator for this object
   */
  public Configurator getConfigurator() {
    return configurator;
  }

  /**
   * Starting a server for listening of clients requests.
   *
   * @return true if server is started without the errors
   * @exception  IOException  if an I/O error occurs.
   */
  public void start() throws Exception {
    super.start();

    load();
  }

  /**
   * Stopping a server
   *
   * @return true if server is stopped without the errors
   * @exception  IOException  if an I/O error occurs.
   */
  public void stop() throws Exception {
    save();

    environment.clear();
  }

  /**
   * Loads persistent information
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void load() throws IOException {
    NameGenerator sessionNameGen = null;

    if(!new File(stateFileName).exists()) {
      sessionNameGen = new NameGenerator("s");
    }
    else {
      FileInputStream fis = new FileInputStream(stateFileName);
      ObjectInputStream ois = new ObjectInputStream(fis);
      try {
        sessionNameGen = (NameGenerator)ois.readObject();
      }
      catch(ClassNotFoundException e) {}

      ois.close();
    }

    if(sessionNameGen == null) {
      throw new IOException("Please remove " + stateFileName + " file and try again.");
    }

    environment.put(ChatServant.SESSION_NAME_GENERATOR_PROPERTY, sessionNameGen);

    environment.put(ChatServant.ROOMS_PROPERTY, new NameableList(ChatRoom.class));
    environment.put(ChatServant.SESSIONS_PROPERTY, new NameableList(Session.class));
    
    RoomNameGenerator roomNameGen = new RoomNameGenerator("r");

    environment.put(ChatServant.ROOM_NAME_GENERATOR_PROPERTY, roomNameGen);

    try {
      isDebug = configurator.getProperty(ChatServant.DEBUG_PROPERTY).equalsIgnoreCase("true");
    }
    catch(Exception e) {
      getLogger().logMessage("<" + ChatServant.DEBUG_PROPERTY + "> property doesn't exist.");
      configurator.put(ChatServant.DEBUG_PROPERTY, new Boolean(false));
    }

    try {
      int maxRoomNumber = Integer.parseInt(configurator.getProperty(MAX_ROOM_NUMBER_PROPERTY));
      roomNameGen.setMaxRoomNumber(maxRoomNumber);
    }
    catch(Exception e) {
      getLogger().logMessage("<" + MAX_ROOM_NUMBER_PROPERTY + "> property doesn't exist.");
      configurator.put(MAX_ROOM_NUMBER_PROPERTY, new Integer(128));
      roomNameGen.setMaxRoomNumber(128);
    }

    try {
      Integer.parseInt(configurator.getProperty(ChatServant.WARNING_LENGTH_PROPERTY));
    }
    catch(Exception e) {
      getLogger().logMessage("<" + ChatServant.WARNING_LENGTH_PROPERTY + "> property doesn't exist.");
      configurator.put(ChatServant.WARNING_LENGTH_PROPERTY, new Integer(55000));
    }

    try {
      Integer.parseInt(configurator.getProperty(ChatServant.LIMIT_LENGTH_PROPERTY));
    }
    catch(Exception e) {
      getLogger().logMessage("<" + ChatServant.LIMIT_LENGTH_PROPERTY + "> property doesn't exist.");
      configurator.put(ChatServant.LIMIT_LENGTH_PROPERTY, new Integer(60000));
    }
  }

  /**
   * Saves persistent information
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void save() throws IOException {
    FileOutputStream fos = new FileOutputStream(stateFileName);
    ObjectOutputStream oos = new ObjectOutputStream(fos);

    oos.writeObject(environment.get(ChatServant.SESSION_NAME_GENERATOR_PROPERTY));

    oos.close();
  }

}
