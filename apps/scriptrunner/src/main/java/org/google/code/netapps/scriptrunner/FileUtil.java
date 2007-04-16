// FileUtil.java

package org.google.code.netapps.scriptrunner;

import java.io.*;

/**
 * This class is a container of static methods for common usage.
 *
 * @version 1.0 05/16/2001
 * @author Alexander Shvets
 */
public class FileUtil {

  /** 
   * Disables creation of this class instances
   */
  private FileUtil() {}

  /**
   * Gets the file content in form of bytes array.
   *
   * @param fileName  the name of file to be converted
   * @return  the content of a file in form of bytes array
   * @exception  IOException  if an I/O error occurs.
   */
  public static byte[] getFileAsBytes(String fileName) throws IOException {
    File file = new File(fileName);
    FileInputStream fis = new FileInputStream(file);
    
    byte buffer[] = new byte[(int)file.length()];
    
    fis.read(buffer);
    
    fis.close();
    
    return buffer;
  }
 
  /**
   * Copies content from archive to a file
   *
   * @exception  IOException if an I/O error occurs.
   */
  public static void writeToFile(byte[] buffer, String fileName)
                     throws IOException {

    OutputStream out = new BufferedOutputStream(
                 new FileOutputStream(fileName), 4096);

    try {
      out.write(buffer);
      out.flush();
    }
    finally {
      if(out != null) {
        out.close();
      }
    }
  }

  /**
   * Deletes file (ordinary file or complete directory)
   *
   * @param file the file object
   */
  public static void deleteFile(File file) {
    if(file.isDirectory()) {
      String[] list = file.list();

      for(int i=0; i < list.length; i++) {
        deleteFile(new File(file + "/" + list[i]));
      }
    }

    file.delete();
  }

  /**
   * Gets the extension for a given file name.
   *
   * @param fileName  the name of file
   * @return  the extension
   */
  public static String getExtension(String fileName) {
    int index = fileName.lastIndexOf(".");
 
    if(index != -1) {
      return fileName.substring(index + 1).toLowerCase();
    }
    
    return null;
  }

}
