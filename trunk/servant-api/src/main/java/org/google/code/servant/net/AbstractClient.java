// AbstractClient.java

package org.google.code.servant.net;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.google.code.servant.util.Logger;

/**
 * Implementation of the "pipe" algorithm.
 *
 * @version 1.1 08/09/2001
 * @author Alexander Shvets
 */
public abstract class AbstractClient implements Client {

  /** The logger object */
  protected Logger logger = new Logger() {
    public void logMessage(String message) {
      System.out.println(message);
    }
  };

  /**
   * Performs the "pipe" operation (writeRequest/readresponse as atomic action)
   *
   * @param request  the request from the client
   * @return  the responses array from the server
   */
  public Object[] pipe(Object request) throws IOException {
    writeRequest(request);

    List responses = new ArrayList();

    while(true) {
      try {
        Object response = readResponse();

        if(response == null) {
          break;
        }

        responses.add(response);
      }
      catch(IOException e) {
        break;
      }
    }

    return responses.toArray();
  }

  /**
   * Gets the logger object
   *
   * @return  the logger object
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Sets the logger object
   *
   * @param logger  the logger object
   */
  public void setLogger(Logger logger) {
    this.logger = logger;
  }

}
