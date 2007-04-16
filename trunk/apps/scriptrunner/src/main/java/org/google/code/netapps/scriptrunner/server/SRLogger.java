// SRLogger.java

package org.google.code.netapps.scriptrunner.server;

import java.io.*;

import org.google.code.servant.util.LogFile;

/**
 * This class is a container of static methods for common usage.
 *
 * @version 1.0 05/16/2001
 * @author Alexander Shvets
 */
public class SRLogger extends LogFile {

  /**
   * Creates a log file with specified file name
   *
   * @param fileName  the name of log file
   * @exception IOException  if an I/O error occurs
   */
  public SRLogger(String fileName) throws IOException {
    super(fileName);
  }

  /**
   * Creates a log file with specified file name and flag
   * that indicates if existing log file should be deleted.
   *
   * @param fileName  the name of log file
   * @param delete  true if file with the same name as log file should be deleted
   * @exception IOException  if an I/O error occurs
   */
  public SRLogger(String fileName, boolean delete) throws IOException {
    super(fileName, delete);
  }

  /**
   * Add new message to a log file
   *
   * @param  message  the message that will be added to a log file
   */
  public void logMessage(String message) {
    super.logMessage(message);

    System.out.println(message);
  }

}
