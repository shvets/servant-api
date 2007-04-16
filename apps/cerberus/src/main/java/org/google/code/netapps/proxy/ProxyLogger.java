// WebLogger.java

package org.google.code.netapps.proxy;

import org.google.code.servant.util.LogFile;

import java.io.IOException;


/**
 * The logger class for proxy server
 *
 * @version 1.0 05/21/2001
 * @author Alexander Shvets
 */
public class ProxyLogger extends LogFile {

  /**
   * Creates a log file with specified file name
   *
   * @param fileName  the name of log file
   * @exception IOException  if an I/O error occurs
   */
  public ProxyLogger(String fileName) throws IOException {
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
  public ProxyLogger(String fileName, boolean delete) throws IOException {
    super(fileName, delete);
  }

  /**
   * Add new message to a logger
   *
   * @param  message  the message that will be added to a log file
   */
  public void logMessage(String message) {
    super.logMessage(message);

    System.out.println(message);
  }

}
