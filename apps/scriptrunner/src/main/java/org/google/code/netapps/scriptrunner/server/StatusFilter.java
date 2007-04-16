// StatusFilter.java

package org.google.code.netapps.scriptrunner.server;

import java.io.*;

/**
 * This class is required for filtering status code and message from 
 * output stream.
 *
 * @version 1.0 07/26/2001
 * @author Alexander Shvets
 */
public class StatusFilter {
  private final static String STATUS_CODE_STRING    = "SRStatus";
  private final static String STATUS_MESSAGE_STRING = "SRMessage";

  private String statusCode;
  private String statusMessage;

  private String text;

  /**
   * Creates new filter.
   *
   * @param text  the text to be parsed
   */
  public StatusFilter(String text) {
    this.text = text;

    try {
      parse();
    }
    catch(IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Parses
   */
  private void parse() throws IOException {
    statusCode = null;
    statusMessage = null;

    CharArrayReader car = new CharArrayReader(text.toCharArray());

    BufferedReader reader = new BufferedReader(car);

    while(true) {
      String line = reader.readLine();

      if(line == null) {
        break;
      }

      if(line.startsWith(STATUS_CODE_STRING)) {
        statusCode = line.substring(STATUS_CODE_STRING.length()+1);
      }
      else if(line.startsWith(STATUS_MESSAGE_STRING)) {
        statusMessage = line.substring(STATUS_MESSAGE_STRING.length()+1);
      }
    }

    reader.close();
  }

  /**
   * Gets the status code
   *
   * @return the status code
   */
  public String getStatusCode() {
    return statusCode;
  }

  /**
   * Gets the status message
   *
   * @return the status message
   */
  public String getStatusMessage() {
    return statusMessage;
  }

}
