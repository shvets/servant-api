// WebServant.java

package org.google.code.netapps.web;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.net.FileNameMap;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.google.code.servant.net.infoworm.InfoWorm;
import org.google.code.servant.net.infoworm.InfoWormServant;

/**
 * This class handles processing details, specific for web server
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class WebServant extends InfoWormServant {
  /** The web server */
  private WebServer server;

  /**
   * Creates new web servant
   *
   * @param server the web server
   */
  public WebServant(WebServer server) {
    super(server);

    this.server = server;
  }

  /**
   * Performs the "service" routine: for ech client's request the servant 
   * should prepare responses array
   *
   * @param request  the request from the client
   * @return  the responses list
   * @exception  IOException  if an I/O error occurs.
   */
  public Object[] service(Object request) throws IOException {    
    List requestHeader = ((InfoWorm)request).getHeader();

    String[] requestTitle = parse((String)requestHeader.get(0));
    String method = requestTitle[0];

    List header = new ArrayList();
    byte[] body;

    if(method.equals("GET")) {
      body = doGet(header, requestTitle[1]);
    }
    else if(method.equals("OPTIONS") || method.equals("HEAD") || 
            method.equals("POST")    || method.equals("PUT") || 
            method.equals("DELETE") || method.equals("TRACE")) {
      prepareHeader(header, "501", "text/html");
      body = prepareError("Not implemented");
    }
    else {
      prepareHeader(header, "400", "text/html");
      body = prepareError("Bad Request");
    }

    InfoWorm response = new InfoWorm();
    response.setHeader(header);
    response.setBody(body);

    return new InfoWorm[] { response };
  }

  /**
   * Performs GET request
   *
   * @param header  the header part of info-worm
   * @param fileName the name of the file
   * @exception  IOException  if an I/O error occurs.
   * @return the response as bytes array
   */
  protected byte[] doGet(List header, String fileName) throws IOException {
    String fullName = server.getRootDirectory() + fileName;

    File file = new File(fullName);
    if(file.isDirectory()) {
      fullName = fullName + server.getIndexName();
      file = new File(fullName);
    }

    if(!file.exists()) {
      prepareHeader(header, "404", "text/html");

      return prepareError("File Not Found");
    }

    if(!file.canRead()) {
      prepareHeader(header, "404", "text/html");

      return prepareError("Permission denied");
    }
    
    String path1 = file.getCanonicalPath();
    //String path2 = new File(server.getRootDirectory()).getCanonicalPath();

    if(!path1.startsWith(path1)) {
      prepareHeader(header, "403", "text/html");
      return prepareError("Forbidden");
    }

    FileNameMap map = URLConnection.getFileNameMap();
    String mimeType = map.getContentTypeFor(fullName);

    prepareHeader(header, "200", mimeType);

    return prepareFile(fullName);
  }

  /**
   * Prepares the error message as html block
   *
   * @param message  the error message
   * @return the message as bytes array
   */
  protected byte[] prepareError(String message) {
    String errMsg = "<html><body><h1>" + message + "</h1></body></html>";

    return errMsg.getBytes();
  }

  /**
   * Prepares the header
   *
   * @param header  the header
   * @param respCode  the response code
   * @param mimeType  the MIME type
   */
  protected void prepareHeader(List header, String respCode, String mimeType) {
    header.add("HTTP/1.0 " + respCode + " OK");
    header.add("Server: Java Web Server");
    header.add("MIME-Version: 1.0");
    header.add("Content-type: " + mimeType);
  }

  /**
   * Prepares the header
   *
   * @param fileName the name of the file
   * @exception  IOException  if an I/O error occurs.
   * @return the response as bytes array
   */
  protected byte[] prepareFile(String fileName) throws IOException {
    File file = new File(fileName);
    FileInputStream fis = new FileInputStream(file);

    byte[] buffer = new byte[(int)file.length()];

    fis.read(buffer);

    fis.close();

    return buffer;
  }

  /**
   * Parses the first line of the request to extract elements like
   * method name, file name etc.
   *
   * @param s the string to be parsed
   * @return the array of strings
   */
  private String[] parse(String s) {
    StringTokenizer st = new StringTokenizer(s);

    String[] answer = new String[3];

    answer[0] = st.nextToken();
    answer[1] = st.nextToken();
    answer[2] = st.nextToken();

    return answer;
  }

}
