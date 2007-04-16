// CacheObject.java

package org.google.code.netapps.proxy;

import org.google.code.servant.net.infoworm.InfoWorm;

import java.io.IOException;
import java.io.Serializable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * This class represents single element of the cache
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class CacheObject implements Serializable {
  /** The name of the file for caching */
  private String fileName;

  /** The exporation date */
  private Date expiration;

  private String urlStr;
  private InfoWorm worm;

  /**
   * Creates new cache object
   * @param urlStr the string
   * @param worm the worm
   */
  public CacheObject(String urlStr, InfoWorm worm) {
    this.urlStr = urlStr;
    this.worm   = worm;
  }

  /**
   * Gets the original URL
   *
   * @return the original URL
   */
  public String getURL() {
    return urlStr;
  }

  /**
   * Gets the info-worm
   *
   * @return the info-worm
   */
  public InfoWorm getInfoWorm() {
    return worm;
  }

  /**
   * Sets the expiration date
   *
   * @param expiration the expiration date
   */
  public void setExpiration(Date expiration) {
    this.expiration = expiration;
  }

  /**
   * Gets the expiration date
   *
   * @return the expiration date
   */
  public Date getExpiration() {
    return expiration;
  }

  /**
   * Gets the file name for caching object
   *
   * @return the file name for caching object
   */
  public String getFileName() {
    if(fileName == null) {
      fileName = NameGenerator.newName();
    }

    return fileName;
  }

  /**
   * Saves the cache object
   *
   * @param cacheDirectory the cache directory
   * @exception  IOException  if an I/O error occurs.
   */
  public void save(String cacheDirectory) throws IOException {
    File f = new File(cacheDirectory + File.separator + getFileName());
    FileOutputStream fos = new FileOutputStream(f);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(this);
    oos.flush();
    fos.close();
  }

  /**
   * Deletes the cache object
   *
   * @param cacheDirectory the cache directory
   */
  public void delete(String cacheDirectory) {
    File file = new File(cacheDirectory + File.separator + getFileName());

    if(file.exists()) {
      file.delete();
    }
  }

}
