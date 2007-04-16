/*
 * @(#)ChatLogFile.java 1.0 08/30/2000
 *
 */

package org.google.code.netapps.chat;

import java.io.*;
import java.text.*;
import java.util.*;

import org.google.code.servant.util.LogFile;

/**
 * This class is used for saving system messages in form of log file
 *
 * @version 1.0 08/30/2000
 * @author Alexander Shvets
 */
public class ChatLogFile extends LogFile {

  /**
   * Creates a log file with specified file name 
   *
   * @param fileName  the name of log file
   * @exception IOException  if an I/O error occurs
   */
  public ChatLogFile(String fileName) throws IOException {
    super(fileName, false);
  }

  /**
   * Add new message to a log file 
   *
   * @param  line the message that will be added to a log file
   * @exception IOException  if an I/O error occurs
   */
  public void logMessage(String line) {
    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
    String dateString = formatter.format(new Date());

    super.logMessage(dateString + " " + line);
  }

}
