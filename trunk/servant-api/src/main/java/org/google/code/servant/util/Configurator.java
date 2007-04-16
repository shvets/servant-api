// Configurator.java

package org.google.code.servant.util;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.Properties;

/**
 * This class represents configurator object for whole project
 *
 * @version 1.0 03/25/2001
 * @author Alexander Shvets
 */
public class Configurator extends Properties {
  /** The name of properties file */
  private String fileName;

  /**
   * Creates new configurator object with the specified file name
   *
   * @param fileName the file name
   */
  public Configurator(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Loads properies
   *
   * @throws IOException if I/O errors occur
   */
  public void load() throws IOException {
    File file = new File(fileName);

    if(!file.exists()) {
      throw new IOException("File " + fileName + " doesn't exist.");
    }

    InputStream is = new BufferedInputStream(new FileInputStream(file));

    super.load(is);

    is.close();
  }

  /**
   * Stores properies
   *
   * @throws IOException if I/O errors occur
   */
  public void store() throws IOException {
    File file = new File(fileName);

    if(!file.canWrite()) {
      throw new IOException("File " + fileName + " cannot be used for write operation.");
    }

    OutputStream os = new BufferedOutputStream(new FileOutputStream(file));

    super.store(os, null);

    os.close();
  }

  /**
   * Sets general property
   *
   * @param key  the key
   * @param value  the value
   * @return the previous value of the specified key in this hashtable,
   * or null if it did not have one.
   */
  public Object setProperty(String key, String value) {
    return put(key, value);
  }

  /**
   * Gets boolean property
   *
   * @param key  the key
   * @param defaultValue  default value
   * @return boolean value that corresponds specified key, or
   * default value if value doesn't exist
   */
  public boolean getBooleanProperty(String key, String defaultValue) {
    String s = getProperty(key, defaultValue);

    return s.equalsIgnoreCase("true");
  }

  /**
   * Gets int property
   *
   * @param key  the key
   * @param defaultValue  default value
   * @return int value that corresponds specified key, or
   * default value if value doesn't exist
   */
  public int getIntProperty(String key, String defaultValue) {
    try {
      return Integer.parseInt(getProperty(key, defaultValue));
    }
    catch(Exception e) {
      return Integer.parseInt(defaultValue);
    }
  }

  /**
   * Gets string property
   *
   * @param key  the key
   * @param defaultValue  default value
   * @return string value that corresponds specified key, or
   * default value if value doesn't exist
   */
  public String getStringProperty(String key, String defaultValue) {
    try {
      return getProperty(key, defaultValue);
    }
    catch(Exception e) {
      return defaultValue;
    }
  }

}
