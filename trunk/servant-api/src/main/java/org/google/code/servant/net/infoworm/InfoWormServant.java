// InfoWormServant.java

package org.google.code.servant.net.infoworm;

import org.google.code.servant.net.AbstractServant;
import org.google.code.servant.net.Server;

import java.io.IOException;
import java.util.List;

/**
 * This class implements servant behavior for infoworm communications
 *
 * @version 1.1 08/09/2001
 * @author Alexander Shvets
 */
public abstract class InfoWormServant extends AbstractServant {
  /** The input stream */
  private InfoWormInputStream in;

  /** The output stream */
  private InfoWormOutputStream out;

  public InfoWormServant(Server server) {
    super(server);
  }

  /**
   * Reads request from user's source
   *
   * @param source the source from which servant can read client's request
   * @return   client's request
   * @exception  IOException  if an I/O error occurs.
   */
  public Object readRequest(Object source) throws IOException {
    in = (InfoWormInputStream)source;

    List requestHeader = in.readHeader();
    byte[] requestBody = null;

    InfoWorm request = new InfoWorm();

    request.setHeader(requestHeader);

    String contentLengthStr = request.getFieldValue(InfoWorm.CONTENT_LENGTH_FIELD);

    if(contentLengthStr != null) {
      try {
        long contentLength = Long.parseLong(contentLengthStr);
        requestBody = in.readBody(contentLength);
      }
      catch(NumberFormatException e) {
        server.getLogger().logMessage(e.toString());
      }
    }

    request.setBody(requestBody);

    return request;
  }

  /**
   * Writes response to client's destination
   *
   * @param response  prepared response
   * @param destination the destination object
   * @exception  IOException  if an I/O error occurs.
   */
  public void writeResponse(Object response, Object destination) throws IOException {
    out = (InfoWormOutputStream)destination;

    out.writeInfoWorm((InfoWorm)response);
  }

  /**
   * Releases all captured resources.
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void release() throws IOException {
/*    if(socket != null) {
      socket.close();
    }
*/

    try {
      if(in != null) {
        in.close();
      }
    }
    catch(IOException e) {
      server.getLogger().logMessage(e.toString());
    }

    try {
      if(out != null) {
        out.close();
      }
    }
    catch(IOException e) {
      server.getLogger().logMessage(e.toString());
    }
  }

}

