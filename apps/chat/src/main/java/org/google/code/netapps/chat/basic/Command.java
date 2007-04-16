/*
 * @(#)Command.java 1.0 07/02/99
 *
 */

package org.google.code.netapps.chat.basic;

/**
 * The class contains constants for supported commands
 *
 * @version 1.0 07/02/99
 * @author Alexander Shvets
 */
public interface Command {

  public String REGISTER = "register";
  public String POLL     = "poll";
  public String EXIT     = "exit";
  public String CRASH    = "crash";
                                
  public String TALK          = "talk";
  public String HELP          = "help";
  public String CUSTOMERS     = "custs";
  public String CSRS          = "csrs";
  public String EXPERTS       = "experts";
  public String SUPERVISORS   = "svs";
  public String ROOMS         = "rooms";
  public String ROOM          = "room";
  public String CREATEROOM    = "cr";
  public String ENTERROOM     = "er";
  public String LEAVEROOM     = "lr";
  public String SELECTROOM    = "sr";
  public String DESTROYROOM   = "dr";
  public String CURRENTROOM   = "current";
  public String WHOAMI        = "whoami";
  public String PRIVATETALK   = "pt";
  public String TALKING       = "talking";
  public String SETCOMMENT    = "sc";
  public String GETCOMMENT    = "gc";
  public String SETOWNER      = "so";
  public String GETOWNER      = "go";
  public String SETVISIBILITY = "sv";
  public String SIZELIMIT     = "sl";

}

