/*
 * @(#)ConsoleChatClient.java 1.01 09/06/99
 *
 */

package org.google.code.netapps.chat;

import java.io.*;

import org.google.code.netapps.chat.primitive.*;
import org.google.code.netapps.chat.basic.*;
import org.google.code.netapps.chat.event.*;
import org.google.code.servant.net.Client;
import org.google.code.servant.net.Interactor;

/**
 * Class for presenting chat client that work with a console.
 *
 * @version 1.01 09/06/99
 * @author Alexander Shvets
 */
public class ConsoleChatInteractor extends ChatInteractor {

  /**
  * Constructs console client with the specified type and name. It will
  * connect with a chat server that run on specified internet address
  * and listening on specified port.
  *
  */
  public ConsoleChatInteractor(Client client, int pollingTime) {
    super(client, pollingTime);
  }

  /**
   * Sends the request from the client
   *
   * @param request the request from the client
   * @exception  IOException  if an I/O error occurs.
   */
  public void request(Object request) throws IOException {
    try {
      if(request.equals(Command.POLL)) {
        if(!txQueue.contains(Command.POLL)) {
          super.request(request);
        }
      }
      else {
        super.request(request);
      }
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Implementation of ChatActionListener prototype method.
   *
   * @param   event event that carries out information-notification
   */
  public void chatPerformed(ChatEvent event) {
    String message = event.getMessage();

    if(message != null) {
      System.out.println(message);
    }

    byte[] body = event.getBody();

    if(body != null && body.length > 0) {
      System.out.println(new String(body));
    }
  }

  public static void main(String args[]) throws Exception {
    String host     = "localhost";
    int port        = 4646;
    int pollingTime = 1000;
    String name     = ParticipantType.CSR + " " + "alex1";
    String password = "aaa";

    if(args.length > 0) host        = args[0];
    if(args.length > 1) port        = Integer.parseInt(args[1]);
    if(args.length > 2) pollingTime = Integer.parseInt(args[2]);
    if(args.length > 4) name        = args[3] + " " + args[4];
    if(args.length > 5) password    = args[5];

    ChatDirectClient client = new ChatDirectClient(host, port);

    client.register(name, password);

    Interactor interactor = new ConsoleChatInteractor(client, pollingTime);

    interactor.start();

    System.out.println(name + " has connected to " + host + ":" + port);
    System.out.println("Type \'" + Command.EXIT + "\' to exit");

    while(true) {
      String request = null;
      try {
        request = (new BufferedReader(new InputStreamReader(System.in))).readLine().trim();
        
        interactor.request(request);

        if(request.equals(Command.EXIT)) {
          break;
        }
      }
      catch(IOException e) {
        e.printStackTrace();
        request = null;
      }
    }

    interactor.stop();
  }

}
