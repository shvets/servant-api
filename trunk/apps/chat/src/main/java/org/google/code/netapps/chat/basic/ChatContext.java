package org.google.code.netapps.chat.basic;

import org.google.code.netapps.chat.primitive.*;
import org.google.code.servant.util.SyncQueue;
import org.google.code.servant.net.Server;
import org.google.code.servant.net.DefaultContext;
import org.google.code.servant.net.Servant;
import org.google.code.servant.net.ServantManager;
import org.google.code.servant.net.infoworm.InfoWorm;

public class ChatContext extends DefaultContext {
  private SyncQueue txBuffer = new SyncQueue();

  private Participant participant;
  private ChatRoom chatRoom;

  /** The name */
  private String name;

  private String password;
  private Server server;

  /**
   *
   *
   * @param name the name
   */
  public ChatContext(String name, String password, Server server) {
    this.name = name;
    this.password = password;
    this.server = server;
  }

  /**
   * Gets the name
   *
   * @return  the name
   */
  public String getName() {
    return name;
  }

  public String getPassword() {
    return password;
  }

  public SyncQueue getTxBuffer() {
    return txBuffer;
  }

  public Participant getParticipant() {
    return participant;
  }

  public void setParticipant(Participant participant) {
    this.participant = participant;
  }

  public ChatRoom getChatRoom() {
    return chatRoom;
  }

  public void setChatRoom(ChatRoom chatRoom) {
    this.chatRoom = chatRoom;
  }

  /**
   * Cancels the context
   *
   */
  public void cancel() {
    StringBuffer body = new StringBuffer();

    String login    = participant.getType() + " " + participant.getName();
    String password = "???";

    InfoWorm infoWorm = new InfoWorm();
    
    infoWorm.setField(Constants.USER_NAME_FIELD, login);
    infoWorm.setField(Constants.PASSWORD_FIELD, password);
    infoWorm.setField(Constants.COMMAND_FIELD, Command.CRASH);

    try {
      ServantManager servantManager = server.getServantManager();

      Servant servant = servantManager.get();

      Object response = servant.service(infoWorm);

      servantManager.release(servant);
    }
    catch(Throwable ex) {
      ex.printStackTrace();
    }
  }

}
