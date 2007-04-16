// InfoWormClient.java

package org.google.code.servant.net.infoworm;

import java.net.Socket;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import org.google.code.servant.net.AbstractClient;

/**
 * This class represent client that works with infoworm objects
 *
 * @version 1.0 05/21/2001
 * @author Alexander Shvets
 */
public class InfoWormClient extends AbstractClient {
  /** The input stream */
  protected transient InfoWormInputStream in;

  /** The output stream */
  protected transient InfoWormOutputStream out;

  /** The host name */
  protected String  host;

  /** The port number */
  protected int port;

  /**
   * Creates new InfoWorm client with the predefined host name
   * and port number.
   */
  public InfoWormClient() {
    this("localhost", 80);
  }

  /**
   * Creates new InfoWorm client.
   *
   * @param host  the host name
   * @param port  the port number
   */
  public InfoWormClient(String  host, int port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Gets the host name.
   *
   * @return  the host name
   */
  public String getHost() {
    return host;
  }

  /**
   * Sets the host name.
   *
   * @param host  the host name
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * Gets the port number.
   *
   * @return  the port number
   */
  public int getPort() {
    return port;
  }

  /**
   * Sets the port number.
   *
   * @param port the port number
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Writes the request from the client to the server.
   *
   * @param request the request from the client.
   * @exception  IOException  if an I/O error occurs.
   */
  public void writeRequest(Object request) throws IOException {
    out.writeInfoWorm((InfoWorm)request);
  }

  /**
   * Reads the response from the server.
   *
   * @return   the response from the server.
   * @exception  IOException  if an I/O error occurs.
   */
  public Object readResponse() throws IOException {
    return in.readInfoWorm();
  }

  /**
   * Performs "pipe" operation (writeRequest/readresponse as atomic action)
   *
   * @param request  the request from the client
   * @return  the responses array from the server
   */
  public Object[] pipe(Object request) throws IOException {
    Socket socket = new Socket(host, port);

    in  = new InfoWormInputStream(
                    new BufferedInputStream(socket.getInputStream()));
    out = new InfoWormOutputStream(
                    new BufferedOutputStream(socket.getOutputStream()));

    Object[] responses = super.pipe(request);

    try {
      if(in != null) {
        in.close();
      }

      if(out != null) {
        out.close();
      }

      if(socket != null) {
        socket.close();
      }
    }
    finally {
      in     = null;
      out    = null;
      socket = null;
    }

    return responses;
  }

}
