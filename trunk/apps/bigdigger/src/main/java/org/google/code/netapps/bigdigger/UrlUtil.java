// URLUtil.java

package org.google.code.netapps.bigdigger;

import java.io.File;
import java.net.URL;

/**
 * The convenient holder of static routines.
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class UrlUtil {

  /**
   * Discables creation of instances of this class
   */
  private UrlUtil() {}

  /**
   * Gets the part of URL without protocol and reference
   *
   * @param url  the url
   * @return the part of URL without protocol and reference
   */
  public static String getHostAndFile(URL url) {
    return (url.getHost() + url.getFile()).replace('/', File.separatorChar);
  }

  /**
   * The string representation of URL without a reference part
   *
   * @param url  the url
   * @return the string representation of URL without a reference part
   */
  public static String toString(URL url) {
    String name = url.toExternalForm();

    int index = name.indexOf("#");

    if(index == -1) {
      return name;
    }

    return name.substring(0, index);
  }

  /**
   * Gets the path part of URL
   *
   * @param url  the url
   * @return the path part of URL
   */
  public static String getPath(URL url) {
    String hostName = url.getHost();
    String fileName = url.getFile();

    int pos = fileName.lastIndexOf("/");

    String dirName = fileName.substring(0, pos+1);

    if(dirName.endsWith("/"))
      dirName = dirName.substring(0, dirName.length()-1);

    return (hostName + dirName).replace('/', File.separatorChar);
  }

}
