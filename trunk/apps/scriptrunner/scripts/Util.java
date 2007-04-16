// Util.java

package com.sageian.scriptrunner;

import java.io.*;

/**
 * This interface is a container for status definitions.
 *
 * @version 1.0 05/15/2001
 * @author Alexander Shvets
 */
public class Util {
  
  private Util() {}

  public static byte[] getFileAsBytes(String fileName) throws IOException {
    File file = new File(fileName);
    FileInputStream fis = new FileInputStream(file);

    byte[] buffer = new byte[(int)file.length()];

    fis.read(buffer);

    fis.close();

    return buffer;
  }

  public static String getExtension(String fileName) throws IOException {
    int index = fileName.lastIndexOf(".");

    if(index != -1) {
      return fileName.substring(index+1);
    }

    return null;
  }

}