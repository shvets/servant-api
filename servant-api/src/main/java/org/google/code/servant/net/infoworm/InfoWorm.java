// InfoWorm.java

package org.google.code.servant.net.infoworm;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * This class represent a data object with the header and the body -
 * to emulate information flow between the client and the server.

 * @version 1.0 03/27/2001
 * @author Alexander Shvets
 */
public class InfoWorm implements Serializable {
  public static final String CONTENT_LENGTH_FIELD = "Content-Length";

  /** The header */
  protected List header = new ArrayList();

  /** The body */
  protected byte[] body;

  /**
   * Creates new infoworm object
   */
  public InfoWorm() {}

  /**
   * Creates new infoworm object with the specified header and body
   *
   * @param header  the àeader
   * @param body  the array with the body content
   */
  public InfoWorm(List header, byte[] body) {
    this.header = header;
    this.body = body;
  }

  /**
   * Gets the header of infoworm
   *
   * @return  the header of infoworm
   */
  public List getHeader() {
    return header;
  }

  /**
   * Sets the header of infoworm
   *
   * @param header  the header of infoworm
   */
  public void setHeader(List header) {
    this.header = header;
  }

  /**
   * Gets the body of infoworm
   *
   * @return  the body of infoworm
   */
  public byte[] getBody() {
    return body;
  }

  /**
   * Sets the body of infoworm
   *
   * @param body  the body of infoworm
   */
  public void setBody(byte[] body) {
    this.body = body;
  }

  /**
   * Gets specified with the key field of header
   *
   * @param  the key
   * @return  specified with the key field of header
   */
  public String getFieldValue(String key) {
    for(int i=0; i < header.size(); i++) {
      String field = (String)header.get(i);
      int pos = field.indexOf(":");

      if(pos == -1)
        continue;

      String key2 = field.substring(0, pos).trim();

      if(key.equalsIgnoreCase(key2)) {
        return field.substring(pos+1).trim();
      }
    }

    return null;
  }

  /**
   * Sets new field in a header. This string should contain ":" delimiter
   *
   * @param  the filed that will be added
   */
  public void setField(String field) {
    setField(field, null);
  }

  /**
   * Sets new field in a header.
   *
   * @param key  the left part of the field
   * @param value the right part of the field
   */
  public void setField(String key, String value) {
    int index = -1;

    for(int i=0; i < header.size(); i++) {
      String line = (String)header.get(i);

      if(line.startsWith(key)) {
        index = i;
        break;
      }
    }

    String line = null;

    if(value == null) {
      line = key;
    }
    else {
      line = key + ": " + value;
    }

    if(index == -1) {
      header.add(line);
    }
    else {
      header.set(index, line);
    }
  }

  /**
   * Returns a string representation of the object.
   *
   * @return  a string representation of the object.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer("header: " + header.size() + "\n");

    for(int i=0; i < header.size(); i++) {
      String field = (String)header.get(i);
      sb.append(field + "\n");
    }

    if(body != null) {
      sb.append("body: " + body.length + "\n");
      for(int i=0; i < body.length; i++) {
        sb.append(body[i] + "\n");
      }
    }
    else {
      sb.append("body: none" + "\n");
    }

    sb.append("\n");

    return sb.toString();
  }

}
