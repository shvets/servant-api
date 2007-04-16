// DirectConnectionFactory.java

package org.google.code.servant.net.direct;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.InetAddress;

import org.google.code.servant.net.ConnectionFactory;
import org.google.code.servant.net.Server;
import org.google.code.servant.net.Connection;

/**
 * This class the factory for direct connections.
 *
 * @version 1.0 08/09/2001
 * @author Alexander Shvets
*/
public class DirectConnectionFactory implements ConnectionFactory {
  /** The constant that specifies maximum number of connection by default */
  public static final int MAX_CONNECTIONS = 128;

  /** The server socket object which is used by server for listening users requests */
  protected ServerSocket serverSocket;

  /** The maximum number of connection  */
  protected int maxConnections = MAX_CONNECTIONS;

  /** The host name for this server */
  protected String host = "localhost";

  /** The port number for this server */
  protected int port;

  /** The soTimeout value for server socket object */
  protected int soTimeout;

  /** The server */
  protected Server server;

  /**
   * Creates new direct connection factory
   *
   * @param server  the server
   */
  public DirectConnectionFactory(Server server) {
    this.server = server;
  }

  /**
   * Gets the host name.
   *
   * @return the host name
   */
  public String getHost() {
    return host;
  }

  /**
   * Sets host name.
   *
   * @param host the host name
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * Gets port number.
   *
   * @return port number.
   */
  public int getPort() {
    return port;
  }

  /**
   * Sets the port number.
   *
   * @param  port number.
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Gets soTimeout value.
   *
   * @return soTimeout value.
   */
  public int getSoTimeout() {
    return soTimeout;
  }

  /**
   * Sets soTimeout value.
   *
   * @param  soTimeout value.
   */
  public void setSoTimeout(int soTimeout) {
    this.soTimeout = soTimeout;
  }

  /**
   * Set up maximum number of connections for ServerSocket object.
   * By default this value is equals to MAX_CONNECTIONS.
   *
   */
  public void setMaxConnections(int maxConnections) {
    this.maxConnections = maxConnections;
  }

  /**
   * Initializes the direct connection factory
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void init() throws IOException {
    try {
      serverSocket = new ServerSocket(port, MAX_CONNECTIONS,
                                      InetAddress.getByName(host));
      serverSocket.setSoTimeout(soTimeout);
    }
    catch(Throwable t) {
      server.getLogger().logMessage(t.toString());
      serverSocket = null;
    }
  }

  /**
   * Cleans up the direct connection factory
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void cleanUp() throws IOException {
    try {
      serverSocket.close();
    }
    catch (IOException e) {
      server.getLogger().logMessage(e.toString());
    }

    serverSocket = null;
  }

  /**
   * Creates the new direct connection
   *
   * @return  the new direct connection
   * @exception  IOException  if an I/O error occurs.
   */
  public Connection create() throws IOException {
    return new DirectConnection(server, serverSocket.accept());
  }

}
