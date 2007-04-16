// DirectConnection.java

package org.google.code.servant.net.direct;

import java.io.IOException;
import java.net.Socket;

import org.google.code.servant.net.AbstractConnection;
import org.google.code.servant.net.Server;

/**
 * This class represents direct socket connection.
 *
 * @version 1.0 08/09/2001
 * @author Alexander Shvets
 */
public class DirectConnection extends AbstractConnection {
  /** The socket object */
  private Socket socket;

  /**
   * Creates new direct connection.
   *
   * @param server the server
   * @param socket the socket
   */
  public DirectConnection(Server server, Socket socket) {
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
    return socket.getInputStream();
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
    return socket.getOutputStream();
  }

}
