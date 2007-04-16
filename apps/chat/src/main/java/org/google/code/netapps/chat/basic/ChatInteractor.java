/*
 * @(#)ChatInteractor.java 1.0 07/15/99
 *
 */

package org.google.code.netapps.chat.basic;

import java.io.*;
import java.util.*;

import org.google.code.netapps.chat.event.*;
import org.google.code.servant.net.Interactor;
import org.google.code.servant.net.Client;
import org.google.code.servant.net.Poller;
import org.google.code.servant.net.infoworm.InfoWorm;

/**
 * This class represents convenient direct realization of connection
 * with a server on base of Socket object.
 *
 * @version 1.0 07/15/99
 * @author Alexander Shvets
 */
public abstract class ChatInteractor extends Interactor implements ChatListener {
  /** This object reads response stream from a server */
  protected EventGenerator eventGenerator;

  /** The poller object */
  protected Poller poller;

  /*
   * A constructor that creates an instance of DirectClient with specified
   * parameters:
   *
   */
  public ChatInteractor(Client client, int pollingTime) {
    super(client);

    eventGenerator = new EventGenerator(pollingTime, this);

    poller = new DefaultPoller(client, pollingTime);
  }

  /**
   * Start an interaction cycle
   */
  public void start() {
    super.start();

    eventGenerator.addChatListener(this);

    eventGenerator.start();

    poller.start();
  }

  /**
   * Stop an interaction cycle
   */
  public void stop() {
    try {
      request(Command.EXIT);
    }
    catch(IOException e) {
      e.printStackTrace();
    }

    eventGenerator.stop();
    eventGenerator.removeChatListener(this);

    poller.stop();

    super.stop();
  }

  /**
   * Check if request contains a command that will stop interaction
   *
   * @param  request  the request from a user
   * @return true if the request contains a command that will stop interaction
   */
  public boolean isExit(Object request) {
    if(request instanceof String) {
      StringTokenizer st = new StringTokenizer((String)request);

      if(st.hasMoreTokens()) {
        return st.nextToken().equalsIgnoreCase(Command.EXIT);
      }
    }
    else if(request instanceof InfoWorm) {
      InfoWorm infoWorm = (InfoWorm)request;

      String command = infoWorm.getFieldValue(Constants.COMMAND_FIELD);
      
      return command.equalsIgnoreCase(Command.EXIT);
    }

    return false;
  }

}

