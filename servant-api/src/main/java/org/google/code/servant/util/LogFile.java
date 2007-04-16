// LogFile.java

package org.google.code.servant.util;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;

/**
 * This class represents log file object
 *
 * @version 1.0 03/25/2001
 * @author Alexander Shvets
 */
public class LogFile implements Logger, Serializable {
  static final long serialVersionUID = -2269840673496056630L;

  private String lineSeparator;

  /** An object that performs writing of log information */
  private BufferedOutputStream os;

  /**
   * Creates a log file with specified file name
   *
   * @param fileName  the name of log file
   * @exception IOException  if an I/O error occurs
   */
  public LogFile(String fileName) throws IOException {
    this(fileName, false);
  }

  /**
   * Creates a log file with specified file name and flag
   * that indicates if existing log file should be deleted.
   *
   * @param fileName  the name of log file
   * @param delete  true if file with the same name as log file should be deleted
   * @exception IOException  if an I/O error occurs
   */
  public LogFile(String fileName, boolean delete) throws IOException {
    File file = new File(fileName);
    if(delete && file.exists()) {
      file.delete();
    }

    os = new BufferedOutputStream(new FileOutputStream(fileName, true));

    lineSeparator = (String)java.security.AccessController.doPrivileged(
                    new sun.security.action.GetPropertyAction("line.separator"));
  }

  public OutputStream getOutputStream() {
    return os;
  }

  /**
   * Add new message to a log file
   *
   * @param  message  the message that will be added to a log file
   */
  public void logMessage(String message) {
    try {
      os.write(message.getBytes());
      os.write(lineSeparator.getBytes());

      os.flush();
    }
    catch(IOException e) {
      System.err.println("Error writing message: " + e);
    }
  }

}
