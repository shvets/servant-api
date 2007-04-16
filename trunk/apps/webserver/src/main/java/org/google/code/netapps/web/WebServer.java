// WebServer.java

package org.google.code.netapps.web;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.google.code.servant.net.DefaultServer;
import org.google.code.servant.net.ServantFactory;
import org.google.code.servant.net.Servant;
import org.google.code.servant.net.PooledServantManager;
import org.google.code.servant.net.infoworm.InfoWormConnectionFactory;
import org.google.code.servant.util.Configurator;

/**
 * This class represents the web server
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class WebServer extends DefaultServer {
  final static String PROPS_FILE_NAME = "WebServer.properties";

  public final static String LOG_FILE_PROP_NAME   = "logfile";
  public final static String HOST_PROP_NAME       = "host";
  public final static String PORT_PROP_NAME       = "port";
  public final static String SO_TIMEOUT_PROP_NAME = "so-timeout";
  public final static String ROOT_DIR_PROP_NAME   = "root.dir";
  public final static String INDEX_PROP_NAME      = "index";

  public final static String LOG_FILE_DEFAULT     = "WebServer.log";
  public final static String HOST_DEFAULT         = "localhost";
  public final static String PORT_DEFAULT         = "80";
  public final static String SO_TIMEOUT_DEFAULT   = "1000";
//  public static final String TIMEOUT_DEFAULT      = "3000";
  public final static String ROOT_DIR_DEFAULT     = ".";
  public final static String INDEX_DEFAULT        = "index.html";

  /** The configurator object */
  protected Configurator configurator;

  /**
   * Creates new web server
   *
   * @param configFileName  the name of configuration file
   */
  public WebServer(String configFileName) throws IOException {
    configurator = new Configurator(configFileName);

    configurator.load();

    InfoWormConnectionFactory connectionFactory = new InfoWormConnectionFactory(this);

    connectionFactory.setHost(configurator.getProperty(HOST_PROP_NAME, HOST_DEFAULT));
    connectionFactory.setPort(configurator.getIntProperty(PORT_PROP_NAME, PORT_DEFAULT));
    connectionFactory.setSoTimeout(configurator.getIntProperty(SO_TIMEOUT_PROP_NAME, SO_TIMEOUT_DEFAULT));

    setConnectionFactory(connectionFactory);
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
   * Gets the name of the host
   *
   * @return the name of the host
   */
  public String getHost() {
    return configurator.getProperty(HOST_PROP_NAME, HOST_DEFAULT);
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
   * Gets the so-timeout value
   *
   * @return the so-timeout value
   */
  public int getSoTimeout() {
    return configurator.getIntProperty(SO_TIMEOUT_PROP_NAME, SO_TIMEOUT_DEFAULT);
  }

  /**
   * Gets the root directory
   *
   * @return the root directory
   */
  public String getRootDirectory() {
    return configurator.getProperty(ROOT_DIR_PROP_NAME, ROOT_DIR_DEFAULT);
  }

  /**
   * Gets the name of index file
   *
   * @return the name of index file
   */
  public String getIndexName() {
    return configurator.getProperty(INDEX_PROP_NAME, INDEX_DEFAULT);
  }

  public static void main(String args[]) throws IOException {
    final WebServer server = new WebServer(PROPS_FILE_NAME);

    ServantFactory factory = new ServantFactory() {
      public Servant create() {
        return new WebServant(server);
      }
    };

    server.setServantManager(new PooledServantManager(factory));

    server.setLogger(new WebLogger(server.getLoggerName(), true));

    try {
      server.start();
      System.out.println("Web server is waiting for connections on port " +
                         server.getPort() + ".");
      System.out.println("Type \'" + "exit" + "\' to exit");

      while(true) {
        try {
          String line = new BufferedReader(
                            new InputStreamReader(System.in)).readLine();
          if(line.equalsIgnoreCase("exit")) {
            server.stop();
            System.out.println("Web server stopped...");
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

