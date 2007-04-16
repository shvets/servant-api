// CachePool.java

package org.google.code.netapps.proxy;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class represents the cache pool
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class CachePool {
  /** The list of cache objects */
  private List list = new ArrayList();

  /** The cache directory */
  private String cacheDirectory;

  /** The maximum size of the cache pool */
  private int cacheMax;

  /**
   * Creates new cache pool
   *
   * @param cacheDirectory the cache directory
   * @param cacheMax the maximum size of the cache pool
   */
  public CachePool(String cacheDirectory, int cacheMax) {
    this.cacheDirectory = cacheDirectory;
    this.cacheMax       = cacheMax;
  }

  /**
   * Checks if the caching is enabled
   */
  public boolean isCacheEnabled() {
    if(cacheMax <= 0 || cacheMax <= 0) {
      return false;
    }

    return true;
  }

  /**
   * Adds the cache object to the pool
   *
   * @param cache the cache object
   */
  public void addCacheObject(CacheObject cache) {
    if(list.size() < cacheMax) {
      list.add(cache);
     
      try {
        cache.save(cacheDirectory);
      }
      catch(IOException e) {}
      return;
    }

    CacheObject oldestCache = null;
    int oldestIndex         = -1;

    for(int i=0; i < list.size(); i++) {
      CacheObject currCache = (CacheObject)list.get(i);
      if(oldestCache == null) {
        oldestCache = currCache;
        oldestIndex = i;
      }
      else if(oldestCache.getExpiration().after(currCache.getExpiration())) {
        oldestCache = currCache;
        oldestIndex = i;
      }
    }

    oldestCache.delete(cacheDirectory);

    if(oldestIndex < 0)
      oldestIndex = 0;

    list.set(oldestIndex, cache);

    try {
      cache.save(cacheDirectory);
    }
    catch(IOException e) {}
  }

  /**
   * Gets the cache object from the pool
   *
   * @param url the url
   * @return cache the cache object
   */
  public CacheObject getCache(String url) {
    Iterator iterator = list.iterator();

    while(iterator.hasNext()) {
      CacheObject cache = (CacheObject)iterator.next();

      if(cache != null && cache.getURL().equals(url)) {
        return cache;
      }
    }

    return null;
  }

  /**
   * Reads the cache pool from the disk
   */
  public int read() {
    int count = 0;

    String files[] = new File(cacheDirectory).list();
    for(int i = 0; i < files.length; i++) {
      if(list.size() >= cacheMax)
        break;

      String fileName = cacheDirectory + File.separator + files[i];

      if(new File(fileName).isDirectory())
        continue;

      if(!fileName.endsWith("cache"))
        continue;

      try {
        FileInputStream fis = new FileInputStream(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        CacheObject cache = (CacheObject)ois.readObject();

        fis.close();
        list.add(cache);
      }
      catch(Exception e) {
        System.out.println(e);
      }

      count++;
    }

    return count;
  }

}
