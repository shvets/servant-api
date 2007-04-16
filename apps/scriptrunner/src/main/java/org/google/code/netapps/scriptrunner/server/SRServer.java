// SRServer.java

package org.google.code.netapps.scriptrunner.server;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.google.code.servant.net.DefaultServer;
import org.google.code.servant.net.ServantFactory;
import org.google.code.servant.net.PooledServantManager;
import org.google.code.servant.net.infoworm.InfoWormConnectionFactory;
import org.google.code.servant.util.Configurator;

/**
 * This class represents ScriptRunner server.
 *
 * @version 1.0 05/16/2001
 * @author Alexander Shvets
 */
public class SRServer extends DefaultServer {
  static final String PROPS_FILE_NAME                  = "conf" + File.separator + "scriptrunner.properties";
  static final String STAGES_FILE_NAME                 = "conf" + File.separator + "scriptrunner.stages";
//  static final String MOVEMENTS_FILE_NAME              = "conf" + File.separator + "scriptrunner.movements";
  static final String ASSOCIATIONS_FILE_NAME           = "conf" + File.separator + "scriptrunner.associations";

  public static final String LOG_FILE_PROP_NAME        = "logfile";
  public static final String HOST_PROP_NAME            = "host";
  public static final String PORT_PROP_NAME            = "port";
  public static final String SO_TIMEOUT_PROP_NAME      = "so.timeout";
  public static final String TIMEOUT_PROP_NAME         = "timeout";
  public static final String PERSONAL_DIR_PROP_NAME    = "personal.dir";

  public static final String JDBC_DRIVER_PROP_NAME     = "jdbc.driver";
  public static final String JDBC_URL_PROP_NAME        = "jdbc.url";
  public static final String JDBC_USER_PROP_NAME       = "jdbc.user";
  public static final String JDBC_PASSWORD_PROP_NAME   = "jdbc.password";

  public static final String TEMP_DIR_PROP_NAME        = "temp.dir";

  public static final String LOG_FILE_DEFAULT          = "SRServer.log";
  public static final String HOST_DEFAULT              = "localhost";
  public static final String PORT_DEFAULT              = "80";
  public static final String SO_TIMEOUT_DEFAULT        = "1000";
  public static final String TIMEOUT_DEFAULT           = "3000";

  public static final String TEMP_DIR_DEFAULT          = "temp";

  public static final String JDBC_DRIVER_DEFAULT       = "";
  public static final String JDBC_URL_DEFAULT          = "";
  public static final String JDBC_USER_DEFAULT         = "";
  public static final String JDBC_PASSWORD_DEFAULT     = "";

  private String installDir;

  /** This map contains the correspondence between stages and directories */
  private Map stages = new HashMap();

  /** This table contains the information how to move from one stage to another */
//  private List movements = new ArrayList();

  /** This list contains assosiations */
  private List associations = new ArrayList();

//  private java.sql.Connection sqlConnection;

  /** The configurator object */
  protected Configurator configurator;

  /**
   * Creates new ScriptRunner server
   *
   * @param  configFileName  the name of file with configuration information
   * @exception  IOException  if an I/O error occurs.
   */
  public SRServer(String configFileName) throws IOException, SQLException {
    installDir = System.getProperty("install.dir");

    if(installDir == null) {
      installDir = new File(".").getCanonicalPath();
    }

    configurator = new Configurator(installDir + File.separator + configFileName);
    configurator.load();

    InfoWormConnectionFactory connectionFactory = new InfoWormConnectionFactory(this);

    connectionFactory.setHost(configurator.getProperty(HOST_PROP_NAME, HOST_DEFAULT));
    connectionFactory.setPort(configurator.getIntProperty(PORT_PROP_NAME, PORT_DEFAULT));
    connectionFactory.setSoTimeout(configurator.getIntProperty(SO_TIMEOUT_PROP_NAME, SO_TIMEOUT_DEFAULT));

//    int timeout = configurator.getIntProperty(TIMEOUT_PROP_NAME, TIMEOUT_DEFAULT);
//    ConnectionManager connectionManager = new TimeoutConnectionManager(connectionFactory, timeout);
//    setConnectionManager(connectionManager);
    setConnectionFactory(connectionFactory);

    readStages(installDir + File.separator + STAGES_FILE_NAME);
//    readMovements(installDir + File.separator + MOVEMENTS_FILE_NAME);
    readAssociations(installDir + File.separator + ASSOCIATIONS_FILE_NAME);

    setLogger(new SRLogger(getLoggerName(), true));

/*
    String url      = getJdbcUrl();
    String user     = getJdbcUserName();
    String password = getJdbcUserPassword();
*/
//    String jdbcDriver = getJdbcDriver();

//    try {
//      Class.forName(jdbcDriver);

/*      sqlConnection = DriverManager.getConnection(url, user, password);

      sqlConnection.setAutoCommit(false);
*/
//    }
//    catch(Exception e) {
//      e.printStackTrace();
//      System.exit(1);
//    }

    ServantFactory factory = new SRServantFactory(this);

    setServantManager(new PooledServantManager(factory));
  }

  /**
   * Reads the information about stages from a file.
   *
   * @param  fileName  the name of the file with stages descriptions
   * @exception  IOException  if an I/O error occurs.
   */
  private void readStages(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));

    while(true) {
      String line = reader.readLine();

      if(line == null) {
        break;
      }

      if(line.startsWith("#") || line.indexOf('#') != -1) {
        continue;
      }

      StringTokenizer st = new StringTokenizer(line);

      if(st.hasMoreTokens()) {
        String stage = st.nextToken();
        
        if(st.hasMoreTokens()) {
          String dirName = st.nextToken();

          stages.put(stage, dirName);
        }
      }
    }
  }

  /**
   * Reads the information about movements between stages from a file.
   *
   * @param  fileName  the name of the file with movements descriptions
   * @exception  IOException  if an I/O error occurs.
   */
/*  private void readMovements(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));

    while(true) {
      String line = reader.readLine();

      if(line == null) {
        break;
      }

      if(line.startsWith("#") || line.indexOf('#') != -1) {
        continue;
      }

      StringTokenizer st = new StringTokenizer(line);

      if(st.hasMoreTokens()) {
        String fromStage = st.nextToken();
        
        if(st.hasMoreTokens()) {
          String toStage = st.nextToken();
          
          if(st.hasMoreTokens()) {
            String backStage = st.nextToken();
          
            movements.add(new Movement(fromStage, toStage, backStage));
          }
        }
      }
    }
  }
*/

  /**
   * Reads the information about associations from a file.
   *
   * @param  fileName  the name of the file with associations descriptions
   * @exception  IOException  if an I/O error occurs.
   */
  private void readAssociations(String fileName) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fileName));

    while(true) {
      String line = reader.readLine();
      
      if(line == null) {
        break;
      }

      if(line.startsWith("#") || line.indexOf('#') != -1) {
        continue;
      }

      StringTokenizer st = new StringTokenizer(line);

      if(st.hasMoreTokens()) {
        String fromStage = st.nextToken();
        
        if(st.hasMoreTokens()) {
          String toStage = st.nextToken();
          
          if(st.hasMoreTokens()) {
            String scriptName = st.nextToken();
            
            if(st.hasMoreTokens()) {
              ArrayList extensions = new ArrayList();
              
              while(st.hasMoreTokens())  {
                String s = st.nextToken();
                
                if(!s.equals(",")) {
                  extensions.add(s);
                }
              }
              
              associations.add(
                  new Association(new StagePair(fromStage, toStage), 
                                  extensions, scriptName));
            }
          }
        }
      }
    }
  }

  /**
   * Gets the directory for given stage
   * @param  stage the stage
   *
   * @return the directory
   */
  public String getDirectory(String stage) {
    Iterator iterator = stages.keySet().iterator();

    while(iterator.hasNext()) {
      String iStage = (String)iterator.next();

      if(stage.equals(iStage)) {
        return (String)stages.get(iStage);
      }
    }

    return null;
  }

  /**
   * Gets the list of stages
   *
   * @return the list of stages
   */
  public Map getStages() {
    return stages;
  }
  
  /**
   * Gets the list of movements between stages
   *
   * @return the list of movements between stages
   */
/*  public List getMovements() {
    return movements;
  }
*/
  /**
   * Gets the list of associations
   *
   * @return the list of associations
   */
  public List getAssociations() {
    return associations;
  }

  /**
   * Gets the SQL connection
   *
   * @return the SQL connection
   */
/*  public java.sql.Connection getSqlConnection() {
    return sqlConnection;
  }
*/
  /**
   * Gets the log file name
   *
   * @return the log file name
   */
  public String getLoggerName() {
    return configurator.getProperty(LOG_FILE_PROP_NAME, LOG_FILE_DEFAULT);
  }

  /**
   * Gets the timeout value
   *
   * @return the timeout value
   */
/*  public int getTimeout() {
    return configurator.getIntProperty(TIMEOUT_PROP_NAME, TIMEOUT_DEFAULT);
  }
*/
  /**
   * Gets the port number
   *
   * @return the port number
   */
  public int getPort() {
    return configurator.getIntProperty(PORT_PROP_NAME, PORT_DEFAULT);
  }

  /**
   * Gets the temporary directory name
   *
   * @return  the temporary directory name
   */
  public String getTemporaryDirectory() {
    return getDirectoryName(configurator.getProperty(TEMP_DIR_PROP_NAME, TEMP_DIR_DEFAULT));
  }

  private String getDirectoryName(String dirName) {
    if(dirName != null) {
      try {
        return new File(dirName).getCanonicalPath();
      }
      catch(IOException e) {}
    }

    return dirName; 
  }

  /**
   * Gets the jdbc driver class name
   *
   * @return the jdbc driver class name
   */
  public String getJdbcDriver() {
    return configurator.getProperty(JDBC_DRIVER_PROP_NAME, JDBC_DRIVER_DEFAULT);
  }

  /**
   * Gets the jdbc url string
   *
   * @return the jdbc url string
   */
  public String getJdbcUrl() {
    return configurator.getProperty(JDBC_URL_PROP_NAME, JDBC_URL_DEFAULT);
  }

  /**
   * Gets the jdbc user name
   *
   * @return the jdbc user name
   */
  public String getJdbcUserName() {
    return configurator.getProperty(JDBC_USER_PROP_NAME, JDBC_USER_DEFAULT);
  }

  /**
   * Gets the jdbc password
   *
   * @return the jdbc password
   */
  public String getJdbcUserPassword() {
    return configurator.getProperty(JDBC_PASSWORD_PROP_NAME, JDBC_PASSWORD_DEFAULT);
  }


  public String getInstallDir() {
    return installDir;
  }

/*  protected void finalize() throws Throwable {
    if(sqlConnection != null) {
      sqlConnection.close();
    }
  }
*/

  /**
   * The main entry to a server program.
   */  
  public static void main(String args[]) throws IOException, SQLException {
    SRServer server = new SRServer(PROPS_FILE_NAME);

    try {
      server.start();
      System.out.println("Script Runner server is waiting for connections on port " + server.getPort() + ".");

      System.out.println("Type 'exit' to exit");
      
      do {
        try {
          while(true) {
            String line = (new BufferedReader(new InputStreamReader(System.in))).readLine();

            if(line == null) {
              continue;
            }
            if(line.equalsIgnoreCase("exit")) {
              break;
            }
          }
          
          server.stop();
          System.out.println("Script Runner server stopped...");
          System.out.println("Press any key...");
          
          (new BufferedReader(new InputStreamReader(System.in))).readLine();
          System.exit(0);
        }
        catch(IOException e) {
          e.printStackTrace();
        }
      } while(true);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

}

