// ZipDocServer.java

package org.google.code.netapps.zipdoc;

import org.google.code.netapps.web.WebServer;
import org.google.code.netapps.web.WebLogger;
import org.google.code.servant.net.Servant;
import org.google.code.servant.net.ServantFactory;
import org.google.code.servant.net.ServantManager;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class represents the zip doc server
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class ZipDocServer extends WebServer {
  final static String PROPS_FILE_NAME         = "ZipDocServer.properties";
  public final static String LOG_FILE_DEFAULT = "ZipDocServer.log";

  /**
   * Creates new zip doc server
   *
   * @param fileName  the name of configuration file
   */
  public ZipDocServer(String fileName) throws IOException {
    super(fileName);
  }

  /** Gets the name of the logger
   *
   * @return the name of the logger
   */
  public String getLoggerName() {
    return configurator.getProperty(LOG_FILE_PROP_NAME, LOG_FILE_DEFAULT);
  }

  public static void main(String args[]) throws IOException {
    final ZipDocServer server = new ZipDocServer(PROPS_FILE_NAME);

    ServantFactory factory = new ServantFactory() {
      public Servant create() {
        return new ZipDocServant(server);
      }
    };

    server.setServantManager(new ServantManager(factory));

    server.setLogger(new WebLogger(server.getLoggerName(), true));

    try {
      server.start();
      System.out.println("ZipDoc Web server is waiting for connections on port " +
                         server.getPort() + ".");
      System.out.println("Type \'" + "exit" + "\' to exit");

      while(true) {
        try {
          String line = new BufferedReader(
                            new InputStreamReader(System.in)).readLine();
          if(line.equalsIgnoreCase("exit")) {
            server.stop();
            System.out.println("ZipDoc server stopped...");
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

