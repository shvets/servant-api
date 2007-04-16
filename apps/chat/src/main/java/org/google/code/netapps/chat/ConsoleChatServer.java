/*
 * @(#)ConsoleChatServer.java 1.01 07/05/99
/ *
 */

package org.google.code.netapps.chat;

import java.io.*;

import org.google.code.netapps.chat.basic.*;
import org.google.code.servant.net.infoworm.InfoWormConnectionFactory;
import org.google.code.servant.net.DefaultContextManager;
import org.google.code.servant.net.ServantFactory;
import org.google.code.servant.net.PooledServantManager;

/**
 * Class for presenting parallel server that output all notifications
 * (results) to System.out.
 *
 * @version 1.01 07/05/99
 * @author Alexander Shvets
 */
public class ConsoleChatServer extends ChatServer {
  final public static String PROPERTIES_FILE_NAME   = "chatserver.properties";
  final public static String SERVER_LOG_FILE_NAME   = "ChatServer.log";
  final public static String SERVER_STATE_FILE_NAME = "ChatServer.ser";

  /**
   * Constructs chat server with the specified parameters.
   *
   * @param   logFileName         the file name for saving log events
   * @param   stateFileName       the file name for saving server's state
   * @param   propsFileName       the file name for reading server's props
   */
  public ConsoleChatServer(String host, int port, int soTimeout) throws IOException {
    super(PROPERTIES_FILE_NAME, SERVER_STATE_FILE_NAME);

    this.setLogger(new ChatLogFile(SERVER_LOG_FILE_NAME));

    environment.put(ChatServant.TRANSCRIPT_SAVER_PROPERTY, new ChatTranscriptSaver());
    environment.put(ChatServant.USER_REGISTER_PROPERTY, new ChatRegister());

    InfoWormConnectionFactory connectionFactory = new InfoWormConnectionFactory(this);

    connectionFactory.setHost(host);
    connectionFactory.setPort(port);
    connectionFactory.setSoTimeout(soTimeout);

//    ConnectionManager connectionManager = new ConnectionManager(connectionFactory);

    setConnectionFactory(connectionFactory);

    setContextManager(new DefaultContextManager());

//    TimeoutContextManager contextManager = new TimeoutContextManager(3000);
//    contextManager.start();
  }

  /**
   * Starting a server for listening of clients requests.
   *
   * @return true if server is started without the errors
   * @exception  IOException  if an I/O error occurs.
   */
  public void start() throws Exception {
    super.start();

    ServantFactory factory = new ChatServantFactory(this);

    // we can create servants only after reading persistent information (super.start())
    setServantManager(new PooledServantManager(factory));
  }

  public static void main(String args[]) throws Exception {
    String host   = "localhost";
    int port      = 4646;
    int soTimeout = 5000;

    final ChatServer server = new ConsoleChatServer(host, port, soTimeout);

    server.start();
    System.out.println("Chat server is waiting for participants " +
                       "on host " + host + " on port " + port + ".");
    System.out.println("Type \'" + Command.EXIT + "\' to exit");

    while(true) {
      try {
        String line = new BufferedReader(
                          new InputStreamReader(System.in)).readLine();
        if(line.equalsIgnoreCase(Command.EXIT)) {
          server.stop();
          System.out.println("Server stopped...");
          System.out.println("Press any key...");
          new BufferedReader(new InputStreamReader(System.in)).readLine();
          System.exit(0);
        }
        if(line.equalsIgnoreCase("restart")) {
          server.stop();
          server.start();
        }
      }
      catch(IOException e) {
        e.printStackTrace();
      }
    }
  }

}
