// HttpClient.java

package org.google.code.netapps.chat.basic;
//package org.javalobby.net.infoworm;

import java.net.*;
import java.io.*;
import java.util.*;

import org.google.code.servant.net.infoworm.InfoWormOutputStream;
import org.google.code.servant.net.infoworm.InfoWormInputStream;
import org.google.code.servant.net.infoworm.InfoWorm;
import org.google.code.servant.net.AbstractClient;

/**
 * This class represent client that works with infoworm objects
 *
 * @version 1.0 05/21/2001
 * @author Alexander Shvets
 */
public class HttpClient extends AbstractClient {
  /** The input stream */
  protected transient InfoWormInputStream in;

  /** The output stream */
  protected transient InfoWormOutputStream out;

  /** The host name */
  protected String  host;

  /** The port number */
  protected int port;

  /** Full URL string for both direct and proxy connection */
  protected String urlString;

  /** name of proxy server */
  private String proxyHost;

  /** port number of proxy server */
  private int proxyPort;

  /** file part of URL */
  private String fileName;

  /**
   * Creates new InfoWorm client with the predefined host name 
   * and port number.
   */
  public HttpClient() {
    this("localhost", 80, null);
  }

  /**
   * Creates new InfoWorm client.
   *
   * @param host  the host name
   * @param port  the port number
   */
  public HttpClient(String  host, int port, String fileName) {
    this.host = host;
    this.port = port;

    this.fileName = fileName;

    urlString = this.fileName;
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
   * Set up an proxy server parameters
   *
   * @param proxyHost   name of proxy server
   * @param proxyPort   port number of proxy server
   */
  public void setProxy(String proxyHost, int proxyPort) {
    this.proxyHost = proxyHost;
    this.proxyPort = proxyPort;

    urlString = "http://" + host + ":" + port  + fileName;
  }

  /**
   * Writes the request from the client to the server.
   *
   * @param request the request from the client.
   * @exception  IOException  if an I/O error occurs.
   */
  public void writeRequest(Object request) throws IOException {
    InfoWorm infoWorm = (InfoWorm)request;

    List header = infoWorm.getHeader();

    StringBuffer sb = new StringBuffer();

    for(int i=0; i < header.size(); i++) {
      String line = (String)header.get(i);

      int index = line.indexOf(":");

      if(index != -1) {
        sb.append(line.substring(0, index-1).trim());
        sb.append("=");
        sb.append(URLEncoder.encode(line.substring(0, index+1).trim(), "UTF-8"));

        if(i < header.size()-1) {
          sb.append("&");
        }
      }
    }

    out.write(("POST " + urlString + sb + " HTTP/1.0\n").getBytes());

    out.write("User-Agent: Java\n".getBytes());
    out.write("Content-type: application/x-www-form-urlencoded\n".getBytes());

    out.write("\n".getBytes());

    out.flush();
  }

  /**
   * Reads the response from the server.
   *
   * @return   the response from the server.
   * @exception  IOException  if an I/O error occurs.
   */
  public Object readResponse() throws IOException {
    int cnt= 5;

    while(in.available() <= 0 && cnt > 0) {
      try {
        Thread.sleep(250);
        cnt--;
      }
      catch(InterruptedException e) {
        throw new IOException("Connection lost");
      }
    }

    // read header

    while(true) {
      String line = in.readLine(in);

      if(line == null)
        throw new IOException("Connection lost");

      if(line.trim().equals("")) break;
    }

    // read body

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    byte[] buf = new byte[2048];
    int n = 0;
    while(true) {
      n = in.read(buf);
      if(n == -1)
        break;

      baos.write(buf, 0, n);
      baos.flush();
    }

    baos.close();

    return extractInfoWorms(baos.toByteArray());
  }

  /**
   * Extract packages from HTTP request's body
   *
   * @param body   the body of HTTP request
   */
  private InfoWorm[] extractInfoWorms(byte[] body) throws IOException {
    List infoworms = new ArrayList();

    String info = null;
    byte[] data = null;

    if(body != null) {
      String bodyText = new String(body);
  
      BufferedReader reader = new BufferedReader(
                                  new InputStreamReader(
                                      new ByteArrayInputStream(body)));
      boolean done = false;

      while(!done) {
        String line = reader.readLine();

        if(line == null) {
          break;
        }

        if(line.startsWith("<infoworm>")) {
          line = reader.readLine();
          while(!line.startsWith("</infoworm>")) {
            int i1 = line.indexOf("info=");
            if(i1 != -1) {
              info = line.substring(i1+5);
            }

            int i2 = line.indexOf("data=");
            if(i2 != -1) {
              StringBuffer sb = new StringBuffer(line.substring(i2+5));
              while(true) {
                line = reader.readLine();
                if(line == null) {
                  break;
                }
    
                if(line.startsWith("</infoworm>")) {
                  break;
                }

                if(line.startsWith("</body>")) {
                  done = true;
                  break;
                }

                sb.append("\n" + line);
              }
              data = sb.toString().getBytes();
            }

            if(line.startsWith("</infoworm>")) {
              break;
            }

            line = reader.readLine();
          }

          InfoWorm response = new InfoWorm();
          
/*          response.setField(Constants.COMMAND_FIELD, Command.REGISTER);
          response.setField(Constants.USER_NAME_FIELD, login);
          response.setField(Constants.PASSWORD_FIELD, password);
*/
          infoworms.add(response);
        }
      }
    }

    if(infoworms.size() == 0) {
      return null;
    }

    InfoWorm[] ps = new InfoWorm[infoworms.size()];
    infoworms.toArray(ps);

    return ps;
  }

  /**
   * Performs "pipe" operation (writeRequest/readresponse as atomic action)
   *
   * @param request  the request from the client
   * @return  the responses array from the server
   */
  public Object[] pipe(Object request) throws IOException {
//    Socket socket = new Socket(host, port);

    Socket socket = null;

    if(proxyHost != null) {
      socket = new Socket(proxyHost, proxyPort);
    }
    else {
      socket = new Socket(host, port);
    }

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

  /**
   * Try to register an user with specified name and passord
   *
   * @param  name  the name of user
   * @param  password  the password of user
   * @return  true if registration stage finished successfully.
   */
/*  public void register(String name, String password) throws RegistrationException {
    this.name    = name;
    this.password = password;

    InfoWorm response = null;

    try {
      response = (InfoWorm)pipe(Command.REGISTER)[0];
    }
    catch(IOException e) {
//      e.printStackTrace();
      throw new RegistrationException(e.getMessage());
    }

    String status = response.getFieldValue(Constants.STATUS_FIELD);

    boolean isOk = new Boolean(status).booleanValue();

    if(!isOk) {
      throw new RegistrationException(new String(response.getBody()).trim());
    }
    
    String message = response.getFieldValue(Constants.USER_NAME_FIELD);

    if(message.length() > 0) {
      this.name = message;
    }
  }
*/
}
