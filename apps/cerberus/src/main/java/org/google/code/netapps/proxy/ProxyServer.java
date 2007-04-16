// ProxyServer.java

package org.google.code.netapps.proxy;

import org.google.code.servant.net.DefaultServer;
import org.google.code.servant.net.Servant;
import org.google.code.servant.net.ServantFactory;
import org.google.code.servant.net.ServantManager;
import org.google.code.servant.net.infoworm.InfoWormConnectionFactory;
import org.google.code.servant.util.Configurator;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class represents the proxy server
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class ProxyServer extends DefaultServer {
  final static String PROPS_FILE_NAME = "Cerberus.properties";

  public final static String LOG_FILE_PROP_NAME         = "logfile";
  public final static String HOST_PROP_NAME             = "host";
  public final static String PORT_PROP_NAME             = "port";
  public final static String SO_TIMEOUT_PROP_NAME       = "so-timeout";
//  public final static String TIMEOUT_PROP_NAME          = "timeout";
  public final static String CACHE_DIR_PROP_NAME        = "cache.dir";
  public final static String CACHE_MAX_PROP_NAME        = "cache.max";
  public final static String CACHE_EXPIRE_PROP_NAME     = "cache.expire";
  public final static String CONNECTIONS_MAX_PROP_NAME  = "connections.max";
  public final static String PROXY_HOST_PROP_NAME       = "proxy.host";
  public final static String PROXY_PORT_PROP_NAME       = "proxy.port";
  public final static String SAVE_IMAGES_PROP_NAME      = "save.images";
  public final static String SAVED_IMAGES_DIR_PROP_NAME = "saved.images.dir";

  public final static String LOG_FILE_DEFAULT         = "Cerberus.log";
  public final static String HOST_DEFAULT             = "localhost";
  public final static String PORT_DEFAULT             = "3128";
  public final static String SO_TIMEOUT_DEFAULT       = "1000";
//  public static final String TIMEOUT_DEFAULT          = "3000";
  public final static String CACHE_DIR_DEFAULT        = ".";
  public final static String CACHE_MAX_DEFAULT        = "0";
  public final static String CACHE_EXPIRE_DEFAULT     = "0";
  public final static String CONNECTIONS_MAX_DEFAULT  = "48";
  public final static String SAVE_IMAGES_DEFAULT      = "false";
  public final static String SAVED_IMAGES_DIR_DEFAULT = ".";

  protected CachePool cachePool;

  /** The configurator object */
  protected Configurator configurator;

  /**
   * Creates new proxy server
   *
   * @param configFileName  the name of configuration file
   */
  public ProxyServer(String configFileName) throws IOException {
    configurator = new Configurator(configFileName);

    configurator.load();

    InfoWormConnectionFactory connectionFactory = new InfoWormConnectionFactory(this);

    connectionFactory.setHost(configurator.getProperty(HOST_PROP_NAME, HOST_DEFAULT));
    connectionFactory.setPort(configurator.getIntProperty(PORT_PROP_NAME, PORT_DEFAULT));
    connectionFactory.setSoTimeout(configurator.getIntProperty(SO_TIMEOUT_PROP_NAME, SO_TIMEOUT_DEFAULT));
    connectionFactory.setMaxConnections(getConnectionsMax());

    setConnectionFactory(connectionFactory);

    String cacheDirectory = getCacheDirectory();
    if(cacheDirectory != null) {
      File f = new File(cacheDirectory);
      if(!f.exists())
        f.mkdirs();
    }

    if(isSaveImages()) {
      String savedImagesDir = getSavedImagesDirectory();

      if(savedImagesDir != null) {
        File f = new File(savedImagesDir);
        if(!f.exists())
          f.mkdirs();
      }
    }

    cachePool = new CachePool(cacheDirectory, getCacheMax());

    System.out.println("cache size: " + getCacheMax() + ", " +
                       "expires: " + getCacheExpire() + " hr(s)");

    System.out.print("Loading cache...");
    int cnt = cachePool.read();
    System.out.println(" - loaded " + cnt + " files from cache.");
  }

  /**
   * Gets the name of the logger
   *
   * @return the name of the logger
   */
  public String getLoggerName() {
    return configurator.getProperty(LOG_FILE_PROP_NAME, LOG_FILE_DEFAULT);
  }

  /**
   * Gets the name of the cache directory
   *
   * @return the name of the cache directory
   */
  public String getCacheDirectory() {
    return configurator.getProperty(CACHE_DIR_PROP_NAME, CACHE_DIR_DEFAULT);
  }

  /**
   * Gets the maximum size of the cache
   *
   * @return the maximum size of the cache
   */
  public int getCacheMax() {
    return configurator.getIntProperty(CACHE_MAX_PROP_NAME, CACHE_MAX_DEFAULT);
  }

  /**
   * Gets the number of hours after which the cache will be expired
   *
   * @return the number of hours after which the cache will be expired
   */
  public int getCacheExpire() {
    return configurator.getIntProperty(CACHE_EXPIRE_PROP_NAME, CACHE_EXPIRE_DEFAULT);
  }

  /**
   * Gets the maximum number of connections
   *
   * @return the maximum number of connections
   */
  public int getConnectionsMax() {
    return configurator.getIntProperty(CONNECTIONS_MAX_PROP_NAME, CONNECTIONS_MAX_DEFAULT);
  }

  /**
   * Gets the so-timeout value
   *
   * @return the so-timeout value
   */
  public int getSoTimeout() {
    return configurator.getIntProperty(SO_TIMEOUT_PROP_NAME, SO_TIMEOUT_DEFAULT);
  }

  /**
   * Gets the port number
   *
   * @return the port number
   */
  public int getPort() {
    return configurator.getIntProperty(PORT_PROP_NAME, PORT_DEFAULT);
  }

  /**
   * Gets the proxy host name
   *
   * @return the proxy host name
   */
  public String getProxyHost() {
    return configurator.getProperty(PROXY_HOST_PROP_NAME, null);
  }

  /**
   * Gets the proxy port number
   *
   * @return the proxy port number
   */
  public int getProxyPort() {
    return configurator.getIntProperty(PROXY_PORT_PROP_NAME, "-1");
  }

  /**
   * Checks if images should be saved 
   */
  public boolean isSaveImages() {
    return configurator.getBooleanProperty(SAVE_IMAGES_PROP_NAME, SAVE_IMAGES_DEFAULT);
  }

  /**
   * Gets the name of the directory for saving images
   *
   * @return the name of the directory for saving images
   */
  public String getSavedImagesDirectory() {
    return configurator.getProperty(SAVED_IMAGES_DIR_PROP_NAME, SAVED_IMAGES_DIR_DEFAULT);
  }

  /**
   * Checks if the proxy is used
   */
  public boolean usingProxy() {
    String proxyHost = getProxyHost();
    int proxyPort    = getProxyPort();

    if(proxyHost != null && !proxyHost.equals("") && proxyPort >= 0) {
      return true;
    }

    return false;
  }

  /**
   * Gets the cache pool
   *
   * @return the cache pool
   */
  public CachePool getCachePool() {
    return cachePool;
  }

  public static void main(String args[]) throws IOException {
    final ProxyServer server = new ProxyServer(PROPS_FILE_NAME);

    ServantFactory factory = new ServantFactory() {
      public Servant create() {
        return new ProxyServant(server);
      }
    };

    server.setServantManager(new ServantManager(factory));

    server.setLogger(new ProxyLogger(server.getLoggerName(), true));

    try {
      server.start();
      System.out.println("Proxy server is waiting for participants on port " +
                         server.getPort() + ".");
      System.out.println("Type \'" + "exit" + "\' to exit");

      while(true) {
        try {
          String line = new BufferedReader(
                            new InputStreamReader(System.in)).readLine();
          if(line.equalsIgnoreCase("exit")) {
            server.stop();
            System.out.println("Server stopped...");
            System.out.println("Press any key...");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
            System.exit(0);
          }
        }
        catch(IOException e) {
          e.printStackTrace();
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

}

