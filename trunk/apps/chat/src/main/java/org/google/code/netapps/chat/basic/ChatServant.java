/*
 * @(#)ChatServant.java 1.1 09/05/2000
 *
 */

package org.google.code.netapps.chat.basic;

import java.io.*;
import java.util.*;

import org.google.code.netapps.chat.primitive.*;
import org.google.code.servant.net.infoworm.InfoWormServant;
import org.google.code.servant.net.infoworm.InfoWorm;
import org.google.code.servant.net.ContextManager;
import org.google.code.servant.net.Context;
import org.google.code.servant.util.Configurator;
import org.google.code.servant.util.SyncQueue;

/**
 * Class for presenting common part of server representative for chat server.
 *
 *
 * @version 1.1 09/05/2000
 * @author Alexander Shvets
*/
public final class ChatServant extends InfoWormServant
  implements Command, ParticipantType {
  // properties for all users

  public final static String ROOMS_PROPERTY                  = "rooms";
  public final static String SESSIONS_PROPERTY               = "sessions";
  public final static String DEBUG_PROPERTY                  = "debug";
  public final static String ROOM_NAME_GENERATOR_PROPERTY    = "roomname.generator";
  public final static String SESSION_NAME_GENERATOR_PROPERTY = "sessionname.generator";
  public final static String TRANSCRIPT_SAVER_PROPERTY       = "transcript.saver";
  public final static String WARNING_LENGTH_PROPERTY         = "warning.length";
  public final static String LIMIT_LENGTH_PROPERTY           = "limit.length";
  public final static String USER_REGISTER_PROPERTY          = "user.register";

  private static final String INCORRECT_PASSWORD_MESSAGE = "Incorrect password";
  private static final String UNKNOWN_TYPE_MESSAGE       = "Unknown type of chat participant";

  private static final String WARNING_MESSAGE =
                             "This conversation is approaching the maximum length.\n" +
                             "You should conclude this conversation soon.";
  private static final String LIMIT_MESSAGE =
                             "This conversation has reached the maximum length.\n" +
                             "No further chat messages can be sent.\n" +
                             "Please disconnect from the chat server.";

  // General variables (must be setted up only once, at the servant's creation time)

  /** List of all chat rooms */
  private NameableList rooms;

  /** List of all open sessions */
  private NameableList sessions;

  /** The generator of names for chat rooms */
  private RoomNameGenerator roomNameGen;

  private NameGenerator sessionNameGen;

  /** The object that can save chat conversation */
  private TranscriptSaver transcriptSaver;

  /** The object that can chack if user is registered */
  private UserRegister userRegister;

  /** Is debug mode on? */
  private boolean isDebug;

  private int warningLength;

  private int limitLength;

  // User-specific variables (must be setted up each time)

  /** Current user */
  private Participant participant;

  /** The name of current user */
  private String participantName;

  /** Current chat room */
  private ChatRoom chatRoom;

  /** The server which is a parent for this servant */
  protected ChatServer server;

  // General variables (must be setted up only once, at the servant's creation time)

  /** Contexts for all users */
//  protected Map contexts;
  protected ContextManager contextManager;

  // User-specific variables (must be setted up each time)

  /** The context for current user */
  protected ChatContext context;

  protected InfoWorm request;
  
  protected String command;
  protected String name;
  protected String password;

  /**
   * Constructs a servant for a given server.
   *
   * @param   server  the server-parent for this servant
   * @param   socket  the socket by using which servant can listen for user
   */
  public ChatServant(ChatServer server) {
    super(server);

    this.server = server;

    Map environment = server.getEnvironment();

    contextManager = server.getContextManager();

    rooms           = (NameableList)environment.get(ROOMS_PROPERTY);
    sessions        = (NameableList)environment.get(SESSIONS_PROPERTY);
    roomNameGen     = ((RoomNameGenerator)environment.get(ROOM_NAME_GENERATOR_PROPERTY));
    sessionNameGen  = ((NameGenerator)environment.get(SESSION_NAME_GENERATOR_PROPERTY));
    transcriptSaver = (TranscriptSaver)environment.get(TRANSCRIPT_SAVER_PROPERTY);
    userRegister    = (UserRegister)environment.get(USER_REGISTER_PROPERTY);

    Configurator configurator = server.getConfigurator();

    isDebug         = configurator.getBooleanProperty(DEBUG_PROPERTY, "true");
    warningLength   = configurator.getIntProperty(WARNING_LENGTH_PROPERTY, "0");
    limitLength     = configurator.getIntProperty(LIMIT_LENGTH_PROPERTY, "0");
  }

  /**
   * Perform "serve" routine: for user request servant should prepare
   * response object
   * 
   * @param request  the request from user
   * @return  the response from server (i.e. from servant) - an answer
   *          to a user's request
   */
  public Object[] service(Object requestObject) throws IOException {    
    this.request = (InfoWorm)requestObject;

    command  = request.getFieldValue(Constants.COMMAND_FIELD);
    name     = request.getFieldValue(Constants.USER_NAME_FIELD);
    password = request.getFieldValue(Constants.PASSWORD_FIELD);

    InfoWorm[] responses = null;
    
    if(command == null) {
      responses = new InfoWorm[] { prepareResponse(true) };
    }
    else {
      if(command.equalsIgnoreCase(Command.REGISTER)) {
        responses = new InfoWorm[] { register() };
      }
      else if(command.equalsIgnoreCase(Command.EXIT)) {
        responses = new InfoWorm[] { unregister(false) };
      }
      else if(command.equalsIgnoreCase(Command.CRASH)) {
        responses = new InfoWorm[] { unregister(true) };
      }
      else if(command.equalsIgnoreCase(Command.POLL)) {
        responses = getPostBoxMessages();
      }
      else {
        responses = new InfoWorm[] { serviceCommand() };
      }
    }

    return responses;
  }

  /**
   * Try to regiser the user
   *
   * @param name  user's name
   * @param password  user's password
   * @return  the response from server
   */
  protected InfoWorm register() {
    String fullName = name;

    if(!userRegister.isRegistered(fullName, password)) {
      return prepareResponse(false, INCORRECT_PASSWORD_MESSAGE);
    }

    String type = fullName.substring(0, fullName.indexOf(" "));
    String name = fullName.substring(fullName.indexOf(" ") + 1);

    if(type.equalsIgnoreCase(ParticipantType.CUSTOMER)) {
      participant = new Customer(name);
    }
    else if(type.equalsIgnoreCase(ParticipantType.CSR)) {
      participant = new CSR(name);
    }
    else if(type.equalsIgnoreCase(ParticipantType.EXPERT)) {
      participant = new Expert(name);
    }
    else if(type.equalsIgnoreCase(ParticipantType.SUPERVISOR)) {
      participant = new Supervisor(name);
    }
    else {
      return prepareResponse(false, UNKNOWN_TYPE_MESSAGE + " : " + fullName);
    }

    participantName = getParticipantName();

    String newName = participant.getQualifiedName();

    request.setField(Constants.USER_NAME_FIELD, newName);

    context = new ChatContext(name, password, server);

    context.setLocked(true);

    contextManager.put(newName, context);

    InfoWorm response = prepareResponse(true);

    context.setParticipant(participant);

    writeDown(participantName + " " + "connected.");

    context.touch();
    context.setLocked(false);

    return response;
  }

  /**
   * Try to unregiser the user
   *
   * @param name  user's name
   * @param password  user's password
   * @param isCrash  true if a cause of unregistering is crash situation
   * @return  the response from server
   */
  protected InfoWorm unregister(boolean isCrash) {
    context = (ChatContext)contextManager.get(name);
  
    if(context == null) {
      return prepareResponse(false, "User " + name + " is not registered.");
    }

    String anotherPassword = context.getPassword();

    if(isCrash || password.equals(anotherPassword)) {
      participant     = context.getParticipant();
      participantName = getParticipantName();
      chatRoom        = context.getChatRoom();

      if(isCrash) { 
        writeDown(participantName + " " + "crashed.");
      }

      synchronized(participant) {
        if(rooms != null) {
          // remove this participant from all the rooms
          NameableList roomsClone = (NameableList)rooms.clone();

          for(int i=0; i < roomsClone.size(); i++) {
            ChatRoom room = (ChatRoom)roomsClone.elementAt(i);
            if(room.contains(participant)) {
              leaveChatRoom(room, isCrash);
            }
          }
        }

        contextManager.remove(name);

        if(!isCrash) {
          writeDown(participantName + " " + "disconnected.");
        }

        if(isDebug) {
          System.out.println("Number of users:      " + contextManager.getContexts().size());
          
          if(rooms != null) {
            System.out.println("Number of chat rooms: " + rooms.size());
          }

          if(sessions != null) {
            System.out.println("Number of sessions:   " + sessions.size());
          }
        }

        return prepareResponse(true);
      }
    }

    return prepareResponse(false, INCORRECT_PASSWORD_MESSAGE);
  }

  /**
   * Get all messages from assosiated with current user post box (tx buffer)
   *
   * @param name  user's name
   * @return  the response from server. It can be a single InfoWorm of
   *          an array of InfoWorms
   */
  protected InfoWorm[] getPostBoxMessages() {
    context = (ChatContext)contextManager.get(name);

    if(context == null) {
      InfoWorm response = prepareResponse(false,
                                 "User " + name + " is not registered.");
      return new InfoWorm[] { response };
    }

    participant = context.getParticipant();

//    participant.setLastTime(System.currentTimeMillis());
    context.touch();

    InfoWorm response = prepareResponse(true);
    
    if(context == null) {
      response.setField(Constants.STATUS_FIELD, "false");

      return new InfoWorm[] { response };
    }

    SyncQueue txQueue = context.getTxBuffer();

    if(txQueue.size() == 0) {
      return new InfoWorm[] { response };
    }

    synchronized(txQueue) {
      InfoWorm[] responses = new InfoWorm[txQueue.size()];

      for(int i=0, sz = txQueue.size(); i < sz; i++) {
        responses[i] = (InfoWorm)txQueue.getAndRemove();
      }

      return responses;
    }
  }

  /**
   * Serve reqest for user name. This mettod performs command with specified
   * attributes.
   *
   * @param name  user's name
   * @param command  command to be executed
   * @param args   arguments of command
   * @exception  IOException  if an I/O error occurs.
   * @return  result of execution - response
   */
  protected InfoWorm serviceCommand() throws IOException {
    InfoWorm response = null;

    context = (ChatContext)contextManager.get(name);

    if(context == null) {
      response  = prepareResponse(false, "User " + name + " is not registered.");
    }
    else {
      String message = request.getFieldValue(Constants.MESSAGE_FIELD);

      participant     = context.getParticipant();
      participantName = getParticipantName();
      chatRoom        = context.getChatRoom();
  
      context.setLocked(true);

      if(command.equalsIgnoreCase(CUSTOMERS)) {
        response  = doCustomers();
      }
      else if(command.equalsIgnoreCase(CSRS)) {
        response  = doCSRs();
      }
      else if(command.equalsIgnoreCase(EXPERTS)) {
        response  = doExperts();
      }
      else if(command.equalsIgnoreCase(SUPERVISORS)) {
        response  = doSupervisors();
      }
      else if(command.equalsIgnoreCase(ROOMS)) {
        response  = doRooms();
      }
      else if(command.equalsIgnoreCase(ROOM)) {
        response  = doRoom(message);
      }
      else if(command.equalsIgnoreCase(CREATEROOM)) {
        response  = doCreateRoom();
      }
     else if(command.equalsIgnoreCase(DESTROYROOM)) {
        response  = doDestroyRoom(message);
      }
      else if(command.equalsIgnoreCase(ENTERROOM)) {
        response  = doEnterRoom(message);
      }
      else if(command.equalsIgnoreCase(SETVISIBILITY)) {
        response  = doSetVisibilityMode(message);
      }
      else if(command.equalsIgnoreCase(LEAVEROOM)) {
        response  = doLeaveRoom(message);
      }
      else if(command.equalsIgnoreCase(SELECTROOM)) {
        response  = doSelectRoom(message);
      }
      else if(command.equalsIgnoreCase(CURRENTROOM)) {
        response  = doCurrentRoom();
      }
      else if(command.equalsIgnoreCase(SETCOMMENT)) {
        response  = doSetComment(message);
      }
      else if(command.equalsIgnoreCase(GETCOMMENT)) {
        response  = doGetComment();
      }
      else if(command.equalsIgnoreCase(SETOWNER)) {
        response  = doSetOwner(message);
      }
      else if(command.equalsIgnoreCase(GETOWNER)) {
        response  = doGetOwner();
      }
      else if(command.equalsIgnoreCase(TALK)) {
        response  = doTalk(message);
      }
      else if(command.equalsIgnoreCase(PRIVATETALK)) {
        response  = doPrivateTalk(message);
      }
      else if(command.equalsIgnoreCase(TALKING)) {
        response  = doTalking();
      }
      else if(command.equalsIgnoreCase(WHOAMI)) {
        response  = doWhoAmI();
      }
      else if(command.equalsIgnoreCase(HELP)) {
        response  = doHelp();
      }
      else {
        response  = prepareResponse(false, "Unrecognized command: " + command);
      }

      context.touch();
      context.setLocked(false);
    }

    return response;
  }

  /**
   * Get customers list
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doCustomers() throws IOException {
    writeDown(participantName + " asks about customers.");

    return prepareResponse(true, getUsers(CUSTOMER));
  }

  /**
   * Get CSRs list
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doCSRs() throws IOException {
    writeDown(participantName + " asks about csrs.");

    return prepareResponse(true, getUsers(CSR));
  }

  /**
   * Get experts list
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doExperts() throws IOException {
    writeDown(participantName + " asks about experts.");

    return prepareResponse(true, getUsers(EXPERT));
  }

  /**
   * Get supervisors list
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doSupervisors() throws IOException {
    writeDown(participantName + " asks about supervisors.");

    return prepareResponse(true, getUsers(SUPERVISOR));
  }

  private String[] getUsers(String type) {
    List selectedUsers = new ArrayList();

    Iterator iterator = contextManager.getContexts().values().iterator();

    while(iterator.hasNext()) {
      ChatContext c = (ChatContext)iterator.next();
      Participant p = c.getParticipant();

      if(p.getType().equals(type)) {
        selectedUsers.add(p.getName());
      }
    }

    String [] array = new String[selectedUsers.size()];
    
    selectedUsers.toArray(array);

    return array;
  }

  /**
   * Get rooms list
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doRooms() throws IOException {
    writeDown(participantName + " asks about chat rooms.");

    return prepareResponse(true, rooms.getNames());
  }

  /**
   * Get context of specified room
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doRoom(String roomName) throws IOException {
    writeDown(participantName + " asks about participants of room " + roomName + ".");

    ChatRoom room = (ChatRoom)rooms.getElement(roomName);

    if(room == null) {
      return prepareResponse(false, "Room " + roomName + " doesn't exist.");
    }
    else {
      String[] names = room.getQualifiedNames();
      String[] names2 = new String[names.length+1];
      names2[0] = roomName;
      System.arraycopy(names, 0, names2, 1, names.length);
      return prepareResponse(true, names2);
    }
  }

  /**
   * Destroy an existing room. It will be removed from chat environment.
   *
   * @param  name       the name of room to be destroyed.
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doDestroyRoom(String roomName) throws IOException {
    ChatRoom room = (ChatRoom)rooms.getElement(roomName);

    if(room == null) {
      return prepareResponse(false, "Room " + roomName + " doesn't exist.");
    }

    removeChatRoom(room, false);

    return prepareResponse(true, roomName);
  }

  /**
   * Enter into an existing room.
   *
   * @param  roomName       the name of room to be entered.
   * @exception  IOException  if an I/O error occurs.
   *
   * @return  response object.
   */
  private InfoWorm doEnterRoom(String roomName) throws IOException {
    ChatRoom room = (ChatRoom)rooms.getElement(roomName);

    if(room == null) {
      return prepareResponse(false, "Room " + roomName + " doesn't exist.");
    }

    if(room.contains(participant)) {
      return prepareResponse(false,
             participantName + " is already inside room " + roomName + ".");
    }

    synchronized(room) {
      boolean customerInRoom = false;

      String type1 = participant.getType();
      for(int i=0; i < room.size(); i++) {
        Participant p = (Participant)room.elementAt(i);
        
        String type2 = p.getType();
        
        if(type1.equals(type2)) {
          return prepareResponse(false,
                 "Unable to join the chat room " + roomName + " because " +
                 firstCap(type1) + " " + firstCap(p.getAlias()) +
                 " is already in the room.\nPlease try again later.");
        }
        if(type2.equals(ParticipantType.CUSTOMER)) {
          customerInRoom = true;
        }
      }
   
      if(participant instanceof Supervisor && !customerInRoom) {
        return prepareResponse(false,
                        "Unable to join the chat room " + roomName +
                        " for " + getParticipantName() +
                        "\nbecause this room doesn't contain any customer.");
      }

      String msg = participantName + " has entered the room " + roomName + ".";

      if(!(participant instanceof Supervisor)) {
        if(writeToTranscript(room, msg) == null) {
          return prepareResponse(true, LIMIT_MESSAGE);
        }
      }

      writeDown(msg);

      context.setChatRoom(room);
  
      room.add(participant);

      String reply = roomName + " " + participant.getQualifiedName();
  
      if(participant instanceof Supervisor) {
        room.setVisibilityMode(ChatRoom.SUPERVISOR_IS_NOT_VISIBLE);
  
        return prepareResponse(true, reply);
      }

      // inform all participants about entering new participant
      for(int i=0; i < room.size(); i++) {
        Participant p = (Participant)room.elementAt(i);
        if(participant != p) {
          writeToPostBox(p.getQualifiedName(), ENTERROOM, true, roomName, reply);
        }
      }
  
      return prepareResponse(true, reply);
    }
  }

  /**
   * Leave an existing room.
   *
   * @param  name       the name of room to be leaved.
   * @exception  IOException  if an I/O error occurs.
   *
   * @return  response object.
   */
  private InfoWorm doLeaveRoom(String name) throws IOException {
    ChatRoom room = (ChatRoom)rooms.getElement(name);

    if(room == null) {
      return prepareResponse(false, "Room " + name + " doesn't exist.");
    }
    else if(!room.contains(participant)) {
      return prepareResponse(false,
                      participantName + " doesn't occupy room " + name + ".");
    }

    synchronized(room) {
      leaveChatRoom(room, false);

      return prepareResponse(true, name + " " + participant.getQualifiedName());
    }
  }

  /*
   * Leave the chat room
   */
  private void leaveChatRoom(ChatRoom room, boolean isCrash) {
    String name = room.getName();

    String msg   = participantName + " has left the room " + name + ".";
    String reply = name + " " + participant.getQualifiedName();
    boolean status = true;

    if(isCrash) {
      msg   = participantName + " has left the room " + name + " (crash).";
      reply = name + " " + participant.getQualifiedName() + " (crash).";
 //     resp  = LEAVEROOM + " " + true + " " + name + " " + true;
    }

    synchronized(room) {
      context.setChatRoom(null);
      room.remove(participant);
  
      writeDown(msg);
  
      String mode = room.getVisibilityMode();
  
      if(participant instanceof Supervisor) {
        if(mode.equalsIgnoreCase(ChatRoom.SUPERVISOR_IS_NOT_VISIBLE)) {
          // do nothing
        }
        else if(mode.equalsIgnoreCase(ChatRoom.SUPERVISOR_IS_VISIBLE_FOR_CSR)) {
          // inform only csrs about leaving participant
          for(int i=0; i < room.size(); i++) {
            Participant p = (Participant)room.elementAt(i);
            if(p instanceof CSR) {
              writeToPostBox(p.getQualifiedName(), Command.LEAVEROOM, status, name, reply);
            }
          }

          writeToTranscript(room, "<private>" + msg);
        }
        else if(mode.equalsIgnoreCase(ChatRoom.SUPERVISOR_IS_VISIBLE_FOR_ALL)) {
          // inform all participants about leaving participant
          for(int i=0; i < room.size(); i++) {
            Participant p = (Participant)room.elementAt(i);
            writeToPostBox(p.getQualifiedName(), Command.LEAVEROOM, status, name, reply);
          }

          writeToTranscript(room, msg);
        }
      }
      else {
        // inform all participants about leaving participant
        for(int i=0; i < room.size(); i++) {
          Participant p = (Participant)room.elementAt(i);
          writeToPostBox(p.getQualifiedName(), Command.LEAVEROOM, status, name, reply);
        }

        writeToTranscript(room, msg);
      }
  
      if(participant instanceof Supervisor) {
        room.setVisibilityMode(ChatRoom.SUPERVISOR_IS_NOT_VISIBLE);
      }
  
      if(participant instanceof CSR) {
        removeChatRoom(room, isCrash);
      }
    }
  }

  /**
   * Remove chat room.
   */
  private void removeChatRoom(ChatRoom room, boolean isCrash) {
    String name = room.getName();
    String mode = room.getVisibilityMode();

    ChatRoom cloneRoom = (ChatRoom)room.clone();
    for(int i=0; i < cloneRoom.size(); i++) {
      Participant p = (Participant)cloneRoom.elementAt(i);
      String pName  = p.getQualifiedName();

      String msg   = firstCap(p.getType()) + " " +  firstCap(p.getAlias()) +
                     " has left the room " + name + ".";
      String reply = name + " " + p.getQualifiedName();
      String resp  = LEAVEROOM + " " + true + " " + name + " " + true;

      if(isCrash) {
        msg   = firstCap(p.getType()) + " " +  firstCap(p.getAlias()) +
                         " has left the room " + name + " " + "(forcibly).";
        reply = name + " " + p.getQualifiedName() + " " + "(forcibly).";
      }

      writeToPostBox(pName, Command.LEAVEROOM, true, name, reply);

      room.remove(p);

      writeDown(msg);

      if(p instanceof Supervisor &&
         !mode.equalsIgnoreCase(ChatRoom.SUPERVISOR_IS_VISIBLE_FOR_ALL)) {
        // do nothing
      }
      else {
        writeToTranscript(chatRoom, msg);
      }
    }

    rooms.remove(room);

    roomNameGen.release(name);

    Interaction interaction = room.getInteraction();

    Session session = interaction.getSession();

    sessions.remove(session);

    transcriptSaver.save(interaction.getTranscript());

    if(isDebug) {
      System.out.println("Transcript for chat room " + name + " saved.");
    }

    writeDown("Room " + name + " destroyed.");

    try {
      server.save();
    }
    catch(IOException e) {
      writeDown(e.toString());
    }
  }

  /**
   * Set visibility mode.
   *
   * @param  roomName       the name of room to be entered.
   * @exception  IOException  if an I/O error occurs.
   *
   * @return  response object.
   */
  private InfoWorm doSetVisibilityMode(String mode) throws IOException {
    if(chatRoom == null) {
      return prepareResponse(false, "Current room is not defined.");
    }

    if(!(participant instanceof Supervisor)) {
      return prepareResponse(false,
                     "The only supervisor can change visibility mode for room.");
    }
    
    synchronized(chatRoom) {
      String oldMode = chatRoom.getVisibilityMode();

      if(mode.equalsIgnoreCase(oldMode))
        return prepareResponse(true, "");

      chatRoom.setVisibilityMode(mode);

      String name = chatRoom.getName();

      String csrTrigger      = "" + oldMode.charAt(0) + mode.charAt(0);
      String customerTrigger = "" + oldMode.charAt(1) + mode.charAt(1);

      String enterMsg = participantName + " has entered the room " + name + ".";
      String leaveMsg = participantName + " has left the room " + name + ".";

      boolean isPrivateMode = false;

      if(oldMode.equals(ChatRoom.SUPERVISOR_IS_NOT_VISIBLE)) {
        if(mode.equals(ChatRoom.SUPERVISOR_IS_VISIBLE_FOR_CSR)) {
          isPrivateMode = true;
          if(writeToTranscript(chatRoom, "<private>" + enterMsg) == null) {
            InfoWorm response = prepareResponse(true, LIMIT_MESSAGE);
            response.setField(Constants.COMMAND_FIELD, ENTERROOM);

            return response;
          }
        }
        else if(mode.equals(ChatRoom.SUPERVISOR_IS_VISIBLE_FOR_ALL)) {
          if(writeToTranscript(chatRoom, enterMsg) == null) {
            InfoWorm response = prepareResponse(true, LIMIT_MESSAGE);
            response.setField(Constants.COMMAND_FIELD, ENTERROOM);

            return response;
          }
        }
      }
      else if(oldMode.equals(ChatRoom.SUPERVISOR_IS_VISIBLE_FOR_CSR)) {
        if(mode.equals(ChatRoom.SUPERVISOR_IS_VISIBLE_FOR_ALL)) {
          if(writeToTranscript(chatRoom, enterMsg) == null) {
            InfoWorm response = prepareResponse(true, LIMIT_MESSAGE);
            response.setField(Constants.COMMAND_FIELD, ENTERROOM);

            return response;
          }
          else if(mode.equals(ChatRoom.SUPERVISOR_IS_NOT_VISIBLE)) {
            if(writeToTranscript(chatRoom, "<private>" + leaveMsg) == null) {
              InfoWorm response = prepareResponse(true, LIMIT_MESSAGE);
              response.setField(Constants.COMMAND_FIELD, LEAVEROOM);

              return response;
            }
          }
        }
      }
      else if(oldMode.equals(ChatRoom.SUPERVISOR_IS_VISIBLE_FOR_ALL)) {
        if(mode.equals(ChatRoom.SUPERVISOR_IS_VISIBLE_FOR_CSR)) {
          if(writeToTranscript(chatRoom, leaveMsg) == null) {
            InfoWorm response = prepareResponse(true, LIMIT_MESSAGE);
            response.setField(Constants.COMMAND_FIELD, LEAVEROOM);

            return response;
          }
        }
        else if(mode.equals(ChatRoom.SUPERVISOR_IS_NOT_VISIBLE)) {
          if(writeToTranscript(chatRoom, "<private>" + leaveMsg) == null) {
            InfoWorm response = prepareResponse(true, LIMIT_MESSAGE);
            response.setField(Constants.COMMAND_FIELD, LEAVEROOM);

            return response;
          }
        }
      }

      String reply = name + " " + participant.getQualifiedName() + " " + isPrivateMode;

      // inform all participants about changing in silent mode
      for(int i=0; i < chatRoom.size(); i++) {
        Participant p = (Participant)chatRoom.elementAt(i);
        String pName = p.getQualifiedName();

        if(p instanceof CSR) {
          if(csrTrigger.charAt(0) == '0' && csrTrigger.charAt(1) == '1') {
            writeToPostBox(pName, ENTERROOM, true, name, reply);
          }
          else if(csrTrigger.charAt(0) == '1' && csrTrigger.charAt(1) == '0') {
            writeToPostBox(pName, LEAVEROOM, true, name, reply);
          }
        }
        else if(p instanceof Customer) {
          if(customerTrigger.charAt(0) == '0' && customerTrigger.charAt(1) == '1') {
            writeToPostBox(pName, ENTERROOM, true, name, reply);
          }
          else if(customerTrigger.charAt(0) == '1' && customerTrigger.charAt(1) == '0') {

            if(writeToTranscript(chatRoom, leaveMsg) == null) {
              InfoWorm response = prepareResponse(true, LIMIT_MESSAGE);
              response.setField(Constants.COMMAND_FIELD, LEAVEROOM);

              return response;
            }
            writeToPostBox(pName, LEAVEROOM, true, name, reply);
          }
        }
      }

      return prepareResponse(true, "");
    }
  }

  /**
   * Select an existing room. It should be from the list of entered rooms
   * for user, assosiated with this servant.
   *
   * @param  name       the name of room to be seleced.
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doSelectRoom(String name) throws IOException {
    ChatRoom room = (ChatRoom)rooms.getElement(name);

    if(room == null) {
      return prepareResponse(false, "Room " + name + " doesn't exist.");
    }
    else if(!room.contains(participant)) {
      return prepareResponse(false,
           "Room " + room + " doesn't contain " + participantName + ".");
    }
    else {
      context.setChatRoom(room);
      writeDown(participantName + " selects room " + name + ".");
      return prepareResponse(true, name);
    }
  }

  /**
   * Get currently selected room.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doCurrentRoom() throws IOException {
    writeDown(participantName + " asks about current room.");

    if(chatRoom == null) {
      return prepareResponse(false, "Current room is not defined.");
    }

    return prepareResponse(true, chatRoom.getName());
  }

  /**
   * Create a new chat room.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doCreateRoom() throws IOException {
    ChatRoom room = createChatRoom();

    if(room == null) {
      writeDown("Limit for rooms names was exceeded.");
      return prepareResponse(false, "Limit for rooms names was exceeded.");
    }

    synchronized(room) {
      Session session         = createSession();
      Interaction interaction = session.newInteraction();

      room.setInteraction(interaction);

      String msg = participantName + " has entered the room " + room.getName() + ".";
    
      if(writeToTranscript(room, msg) == null) {
        return prepareResponse(true, LIMIT_MESSAGE);
      }

      sessions.add(session);

      room.add(participant);
    
      context.setChatRoom(room);

      writeDown("Session " + session.getName() + " created.");
      writeDown("Interaction " + interaction.getName() + " created.");
      writeDown("Room " + room.getName() + " created.");

      writeDown(msg);

      String text = session.getName() + " " + interaction.getName() + " " + room.getName();
   
      return prepareResponse(true, text);
    }
  }

  /**
   * Set a comment for current room's verbatim.
   *
   * @param  comment       the comment
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doSetComment(String comment) throws IOException {
    if(chatRoom == null) {
      return prepareResponse(false, "Current room is not defined.");
    }

    synchronized(chatRoom) {
      String name = chatRoom.getName();
      writeDown(participantName + " set up comment for room " + "\"" + name + "\".");
      Interaction interaction = chatRoom.getInteraction();
      interaction.setComment(comment);
      int sz = chatRoom.size();
      for(int i=0; i < sz; i++) {
        Participant p = (Participant)chatRoom.elementAt(i);
        if(p instanceof Customer) continue;

        writeToPostBox(p.getQualifiedName(), SETCOMMENT, true, name, comment);
      }
  
      return prepareResponse(true, "");
    }
  }

  /**
   * Get a comment for current room's verbatim.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doGetComment() throws IOException {
    if(chatRoom == null) {
      return prepareResponse(false, "Current room is not defined.");
    }

    writeDown(participantName+ " asks about comment for room " +
              "\"" + chatRoom.getName() + "\".");
    Interaction interaction = chatRoom.getInteraction();

    String comment = null;
    if(interaction != null) {
      comment = interaction.getComment();
    }

    return prepareResponse(true, comment);
  }

  /**
   * Set an owner for current room's verbatim.
   *
   * @param  owner       the owner for current room's verbatim.
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doSetOwner(String owner) throws IOException {
    if(chatRoom == null) {
      return prepareResponse(false, "Current room is not defined.");
    }

    String name = chatRoom.getName();

    Interaction interaction = chatRoom.getInteraction();
    interaction.setOwner(owner);
    int sz = chatRoom.size();
    for(int i=0; i < sz; i++) {
      Participant p = (Participant)chatRoom.elementAt(i);
      if(p instanceof Customer) continue;

      writeToPostBox(p.getQualifiedName(), SETOWNER, true, name, owner);
    }

    return prepareResponse(true, owner);
  }

  /**
   * Get an owner for current room's verbatim.
   *
   * @return     the owner for current room's verbatim.
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doGetOwner() throws IOException {
    if(chatRoom == null) {
      return prepareResponse(false, "Current room is not defined.");
    }

    Interaction interaction = chatRoom.getInteraction();

    String owner = null;
    if(interaction != null) {
      owner = interaction.getOwner();
    }

    return prepareResponse(true, owner);
  }

  /**
   * Send a message to all participants of chat room.
   *
   * @param message   the message that will be send.
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doTalk(String args) throws IOException {
    if(chatRoom == null) {
      return prepareResponse(false, "You are not inside any chat room.");
    }

    synchronized(chatRoom) {
      String newMessage = "[" + getParticipantName() + "] " + args;

      newMessage = writeToTranscript(chatRoom, newMessage);
      if(newMessage == null) {
        return prepareResponse(true, LIMIT_MESSAGE);
      }
  
      String name = chatRoom.getName();
      int sz = chatRoom.size();
      for(int i=0; i < sz; i++) {
        Participant p = (Participant)chatRoom.elementAt(i);
        if(!p.equals(participant)) {
          writeToPostBox(p.getQualifiedName(), TALK, true, name, newMessage);
        }
      }
  
      return prepareResponse(true, newMessage);
    }
  }

  /**
   * Send a message to some participant of chat room in private mode.
   *
   * @param type   the type of recepient.
   * @param addressee   the name of recepient.
   * @param message   the message that will be send.
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doPrivateTalk(String args) throws IOException {
    if(chatRoom == null) {
      return prepareResponse(false, "You are not inside any chat room.");
    }

    synchronized(chatRoom) {
      StringTokenizer st = new StringTokenizer(args);
  
      String addressee = st.nextToken() + " " + st.nextToken();
  //    String message = getRestLine(args, addressee);
      String message = "";

      Context c = (ChatContext)contextManager.get(addressee);

      if(c == null) {
        return prepareResponse(false, addressee + " isn't registered on server.");
      }
  
      String newMsg = "[<private> " + participantName + "] " + message;
  
      if(writeToTranscript(chatRoom, newMsg) == null) {
        return prepareResponse(true, LIMIT_MESSAGE);
      }

      writeToPostBox(addressee, PRIVATETALK, true, chatRoom.getName(), newMsg);
  
      return prepareResponse(true, newMsg);
    }
  }

  /**
   * Get context of verbatim for current room.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doTalking() throws IOException {
    if(chatRoom == null) {
      return prepareResponse(false, "Current room is not defined.");
    }

    writeDown(participantName + " asks about talking in current room.");
    Interaction interaction = chatRoom.getInteraction();
    String[] lines = new String[0];
    if(interaction != null) {
      Vector verbatim = interaction.getTranscript().getVerbatim();
      lines = new String[verbatim.size()];
      for(int i=0; i < verbatim.size(); i++) {
        lines[i] = (String)verbatim.elementAt(i);
      }
    }

    return prepareResponse(true, lines);
  }

  /**
   * Get the name of the user.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doWhoAmI() throws IOException {
    writeDown(participantName + " asks about his name.");

    return prepareResponse(true, participant.toString());
  }

  /**
   * Get help info for supported commands.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  private InfoWorm doHelp() throws IOException {
    writeDown(participantName + " asks about help.");

    String[] commands = new String[] {
      HELP +         "\t\t - get help",
      EXIT +         "\t\t - exit from program",

      CUSTOMERS +    "\t   - get list of customers",
      CSRS +         "\t\t - get list of csrs",
      EXPERTS +      "\t   - get list of experts",
      SUPERVISORS +  "\t   - get list of supervisors",
      ROOMS +        "\t\t   - get list of chat rooms",
      ROOM + " name " + "\t  - show participants of specified room",

      CREATEROOM +   "\t   - create new chat room",
      DESTROYROOM +  "\t   - destroy chat room \"name\"",
      ENTERROOM +    "\t   - come into specified room",
      LEAVEROOM +    "\t   - come out from specified room",
      SELECTROOM +   "\t   - select current room for talking",
      CURRENTROOM +  "\t   - let me know in which room I am now",
      WHOAMI +       "\t   - get client's name",
      SETCOMMENT +    "\t   - set comment for current room's verbatim",
      GETCOMMENT +    "\t   - get comment for current room's verbatim",
      SETOWNER +    "\t   - set owner for current room's verbatim",
      GETOWNER +    "\t   - get owner for current room's verbatim",
      TALKING + "\t\t   - get content of verbatim for current room",
      TALK + " mess " + "\t   - send string to chat room",
      PRIVATETALK +  "\t   - send string to registered participant",
    };

    return prepareResponse(true, commands);
  }

  /**
   * Creates new chat room.
   *
   * @return newly created chat room.
   */
  private ChatRoom createChatRoom() {
    String newName = roomNameGen.getNewName();

    if(newName  == null) {
      return null;
    }

    ChatRoom room = new ChatRoom(newName);
    rooms.add(room);

    return room;
  }

  /**
   * Creates new session.
   *
   * @return newly created session.
   */
  private Session createSession() {
    Session session = new Session(sessionNameGen.getNewName());

    sessions.add(session);

    return session;
  }

  private String getParticipantName() {
    return firstCap(participant.getType()) + " " + firstCap(participant.getAlias());
  }

  /**
   * Documenting occured message.
   *
   * @param     text the message to be documented.
   */
  protected void writeDown(String text) {
    server.getLogger().logMessage(text);

    if(isDebug) {
      System.out.println(text);
    }
  }

  protected String writeToTranscript(ChatRoom room, String msg) {
    if(room.isLimitReached()) {
      return null;
    }

    String newMsg = msg;

    Transcript transcript = room.getInteraction().getTranscript();

    if(transcript != null) {
      int size = transcript.size() + msg.length() + 2;

      if(size < limitLength) {
        transcript.append(msg);

        if(size >= warningLength) {
          for(int i=0; i < room.size(); i++) {
            Participant p = (Participant)room.elementAt(i);
            writeToPostBox(p.getQualifiedName(), SIZELIMIT, true, null,
                           WARNING_MESSAGE);
          }
        }
      }
      else {
        int delta = limitLength - transcript.size() - 2;
        newMsg = msg.substring(0, delta);

        transcript.append(newMsg);

        room.setLimitReached(true);

        for(int i=0; i < room.size(); i++) {
          Participant p = (Participant)room.elementAt(i);
          writeToPostBox(p.getQualifiedName(), SIZELIMIT, true, null,
                                LIMIT_MESSAGE);
        }
      }
    }

    return newMsg;
  }

  private void writeToPostBox(String name, String command, boolean status, 
                              String roomName, String reply) {
    ChatContext clientContext = (ChatContext)contextManager.get(name);

    SyncQueue txQueue = clientContext.getTxBuffer();

    InfoWorm infoWorm = prepareResponse(status, reply);
    
    infoWorm.setField(Constants.ROOM_NAME_FIELD, roomName);

    txQueue.add(infoWorm);
  }

  /**
   * Capitalizes first letter of name
   */
  private String firstCap(String name) {
    return Character.toUpperCase(name.charAt(0)) + name.substring(1);
  }

  /**
   * Serve reqest for user name. This mettod performs command with specified
   * attributes.
   *
   * @return  result of execution - response
   */
  protected InfoWorm prepareResponse(boolean status) {
    return prepareResponse(status, (String)null);
  }

  /**
   * Serve reqest for user name. This mettod performs command with specified
   * attributes.
   *
   * @return  result of execution - response
   */
  protected InfoWorm prepareResponse(boolean status, String message) {
    String command = request.getFieldValue(Constants.COMMAND_FIELD);
    String name = request.getFieldValue(Constants.USER_NAME_FIELD);

    InfoWorm response = new InfoWorm();
    
    response.setField(Constants.COMMAND_FIELD, command);
    response.setField(Constants.USER_NAME_FIELD, name);
    response.setField(Constants.STATUS_FIELD, new Boolean(status).toString());

    if(message != null) {
      response.setField(Constants.MESSAGE_FIELD, message);
    }
    
    String roomName = null;

    if(status) {
      if(chatRoom != null) {
        roomName = chatRoom.getName();
      }
    }

    response.setField(Constants.ROOM_NAME_FIELD, roomName);

    return response;
  }

  /**
   * Serve reqest for user name. This mettod performs command with specified
   * attributes.
   *
   * @return  result of execution - response
   */
  protected InfoWorm prepareResponse(boolean status, String[] lines) {
    InfoWorm response = prepareResponse(true);

    StringBuffer sb = new StringBuffer();
    
    for(int i=0; i < lines.length; i++) {
      sb.append(lines[i]);
      if(i < lines.length-1) {
        sb.append("\n");
      }
    }

    response.setBody(sb.toString().getBytes());

    return response;
  }

}
