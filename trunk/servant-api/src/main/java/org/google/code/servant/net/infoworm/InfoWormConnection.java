// InfoWormConnection.java

package org.google.code.servant.net.infoworm;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.net.Socket;

import org.google.code.servant.net.AbstractConnection;
import org.google.code.servant.net.Server;

/**
 * This class represents infoworm connection.
 *
 * @version 1.0 08/09/2001
 * @author Alexander Shvets
 */
public class InfoWormConnection extends AbstractConnection {
  /** The socket object */
  private Socket socket;

  /**
   * Creates new infoworm connection.
   *
   * @param server the server
   * @param socket the socket
   */
  public InfoWormConnection(Server server, Socket socket) {
    super(server);

    this.socket = socket;
  }

  /**
   * Gets the source object from where the connection will read the request
   *
   * @return  the source object from where the connection will read the request
   * @exception  IOException  if an I/O error occurs.
   */
  public Object getSource() throws IOException {
    InputStream is = (InputStream)socket.getInputStream();

    return new InfoWormInputStream(new BufferedInputStream(is));
  }

  /**
   * Gets the destination object, to where the connection will write
   * the response
   *
   * @return  the destination object from where the connection will
   *          read the request
   * @exception  IOException  if an I/O error occurs.
   */
  public Object getDestination() throws IOException {
    OutputStream os = (OutputStream)socket.getOutputStream();

    return new InfoWormOutputStream(new BufferedOutputStream(os));
  }

}
