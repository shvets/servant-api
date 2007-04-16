/*
 * @(#)EJBChatServer.java 1.0 09/13/2000
/ *
 */

package org.google.code.netapps.chat.chat.ejb;

import java.io.*;

import org.google.code.netapps.chat.net.*;
import org.google.code.netapps.chat.chat.defaults.*;
import org.google.code.netapps.chat.chat.basic.*;
import org.google.code.netapps.chat.chat.*;
import org.google.code.servant.util.Configurator;
import org.google.code.servant.net.ServantFactory;

/**
 *
 * @version 1.0 09/13/2000
 * @author Alexander Shvets
 */
public class EJBChatServer extends ChatServer {
  final public static String SERVER_LOG_FILE_NAME      = "chatserver.log";
  final public static String SERVER_STATE_FILE_NAME    = "chatserver.ser";

  private ServantFactory factory;

  /**
   * Constructs chat server with the specified parameters.
   *
   * @param   logFileName         the file name for saving log events
   * @param   stateFileName       the file name for saving server's state
   * @param   propsFileName       the file name for reading server's props
   */
  public EJBChatServer(Configurator configurator) throws IOException {
    super(configurator, SERVER_STATE_FILE_NAME);

    this.setLogger(new ChatLogFile(SERVER_LOG_FILE_NAME));

    environment.put(ChatServant.TRANSCRIPT_SAVER_PROPERTY, new ChatTranscriptSaver());
    environment.put(ChatServant.USER_REGISTER_PROPERTY, new ChatRegister());

    factory = new ChatServantFactory(this);
  }

  /**
   * Starting a server for listening of clients requests.
   *
   * @return true if server is started without the errors
   * @exception  IOException  if an I/O error occurs.
   */
  public boolean start() throws IOException {
    boolean ok = super.start();

    // we can create servants only after reading persistent information (super.start())
    setServantManager(new ServantManager(factory));

    return ok;
  }

  protected Object readRequest(Object source) throws IOException {
    String[] params = (String[])source;

    return new Packet(params[0] + " " + params[1], params[2]);
  }

  protected void writeResponse(Object destination, Object response) throws IOException {
    StringBuffer body  = (StringBuffer)destination;
    Packet[] responses = (Packet[])response;

    body.append("<html><head></head><body>\n");

    for(int i=0; i < responses.length; i++) {
      body.append("<packet>\n");
      body.append("info=" + responses[i].getDescription() + "\n");
      body.append("data=" + new String(responses[i].getData()) + "\n");
      body.append("</packet>\n");
    }

    body.append("</body></html>");
  }

}
