// SRServant.java

package org.google.code.netapps.scriptrunner.server;

import java.io.*;
import java.util.*;
import java.sql.*;


import org.google.code.servant.util.ScriptExecutor;
import org.google.code.servant.net.infoworm.InfoWormServant;
import org.google.code.servant.net.infoworm.InfoWorm;
import org.google.code.netapps.scriptrunner.Constants;
import org.google.code.netapps.scriptrunner.Command;
import org.google.code.netapps.scriptrunner.FileUtil;
import org.google.code.netapps.scriptrunner.Status;

/**
 * This class is a servant for ScriptRunner server.
 *
 * @version 1.0 05/17/2001
 * @author Alexander Shvets
 */
public class SRServant extends InfoWormServant {
//  private java.sql.Connection connection;

  /** An instance of script executor */
  private ScriptExecutor executor = new ScriptExecutor();

  private SRServer server;

  /**
   * Creates new servant object.
   *
   * @param server  the ScriptRunner server
   */
  public SRServant(SRServer server) {
    super(server);

    this.server = server;
  }

  /**
   * Perform request from the user
   *
   * @return  the server's response
   * @exception  IOException  if an I/O error occurs.
   */
//  public InfoWorm[] service(InfoWorm request) throws IOException {
  public Object[] service(Object requestObject) throws IOException {    
    InfoWorm request = (InfoWorm)requestObject;

    InfoWorm response = new InfoWorm();

    String userName = request.getFieldValue(Constants.USER_NAME_FIELD);

    if(userName != null) {
      executor.setUserName(userName);
    }

    String command = request.getFieldValue(Constants.COMMAND_FIELD);

    if(command.equals(Command.PROMOTE)) {
      doPromote(request, response);
    }
    else if(command.equals(Command.GET)) {
      doGet(request, response);
    }
    else if(command.equals(Command.SCRIPT)) {
      doScript(request, response);
    }
    else {
      doError(response, "The command " + command + " is not supported");
    }

    return new InfoWorm[] { response };
  }

  /**
   * Performs the "promote" command.
   *
   * @param  request  the request object
   * @param  response  the response object
   */
  private void doPromote(InfoWorm request, InfoWorm response) {
    String dirName  = request.getFieldValue(Constants.DIRECTORY_NAME_FIELD);
    String fileName = request.getFieldValue(Constants.FILE_NAME_FIELD);

    String fromStage = getStage(dirName);

    if(fromStage == null) {
      doError(response, "The server cannot recognize the \"from\" stage " +
                        "for the directory " + dirName + ".");
      return;
    }

    response.setField(Constants.FROM_STAGE_FIELD, fromStage);

    String extension = FileUtil.getExtension(fileName);

    if(extension == null) {
      doError(response, "There is no extension for ths file: " + fileName);
      return;
    }

//    String toStage = getToStage(fromStage, extension);
    Association association = getAssociation(fromStage, extension);

    if(association == null) {
      doError(response, "The server cannot recognize the \"to\" stage " +
                        "for the \"from\" stage " + fromStage + 
                        " and extension \"" + extension + "\"");
      return;
    }

//    StagePair stagePair = new StagePair(fromStage, toStage);
    StagePair stagePair = association.getStagePair();

    String toStage = stagePair.getToStage();

    response.setField(Constants.TO_STAGE_FIELD, toStage);

//    String scriptName = getScriptName(stagePair, extension);
    String scriptName = association.getScriptName();

    if(scriptName == null) {
      doError(response, "There is no association for extension \"" + 
                        extension + "\" and stages " +
                        "from: " + fromStage + " " + 
                        "to: " + toStage);
      return;
    }
    else {
      String status = Status.PROCESSED;
      String returnedMessage = "";
      
      try {
        String userName = request.getFieldValue(Constants.USER_NAME_FIELD);

        String tag = request.getFieldValue(Constants.TAG_FIELD);
        String comment = request.getFieldValue(Constants.COMMENT_FIELD);

        List params = new ArrayList();

        params.add(server.getInstallDir() + File.separator + scriptName);
        params.add(userName);
        params.add(dirName);
        params.add(fileName);
        params.add(tag);
        params.add(comment);

        int exitValue = executor.execute(params);

        if(exitValue == 0) {
          String answer = executor.getStandardOutput();

          StatusFilter statusFilter = new StatusFilter(answer);

          String statusCode = statusFilter.getStatusCode();

          if(statusCode == null) {
            response.setField(Constants.STATUS_FIELD, Status.PROCESSED);
          }
          else {
            int code = Integer.parseInt(statusCode);

            if(code == 0) {
              response.setField(Constants.STATUS_FIELD, Status.PROCESSED);
            }
            else {
              response.setField(Constants.STATUS_FIELD, Status.FAILED);

              status = statusCode;
              returnedMessage = statusFilter.getStatusMessage();
            }
          }

          response.setBody(answer.getBytes());
        }
        else {
          status = Status.FAILED;

          response.setField(Constants.STATUS_FIELD, Status.FAILED);

          response.setField(Constants.OS_ERROR_NUMBER_FIELD, 
                                  String.valueOf(exitValue));
          response.setBody(executor.getErrorOutput().getBytes());
        }
      }
      catch(IOException e1) {
        status = "Exception";
        returnedMessage = "Error during script execution : " + e1.toString();
        doError(response, "Error during script execution : " + e1.toString());
      }
      catch(InterruptedException e2) {
        status = "Killed";
        returnedMessage = "The script is not responding. Killed.";
        doError(response, "The script " + scriptName + " is not responding. Killed.");
      }

      try {
        addRecord(request, scriptName, fromStage, toStage, status, 
                  returnedMessage);
      }
      catch(Exception e) {
        e.printStackTrace();
      }
    }
  }

  private void addRecord(InfoWorm request, String scriptName, 
                         String fromStage, String toStage, 
                         String status, String returnedMessage)
               throws Exception {
    String sqlString = 
     		"INSERT INTO TASK ( " +
     		"  TASK_ID," +
     		"  REQUESTOR_USER_ID," +
                "  FROM_STAGE," +
                "  TO_STAGE," +
                "  REQUEST_DATE," +
                "  STATUS," +
                "  DIR_NAME," +
                "  FILE_NAME," +
                "  PROMOTE_DATE," +
                "  TAG," +
                "  COMMENT_STRING," +
                "  SCRIPT_NAME," +
                "  MESSAGE_RETURNED" +
     		") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

    PreparedStatement pstmt = null;

//    java.sql.Connection connection = server.getSqlConnection();

    String url      = server.getJdbcUrl();
    String user     = server.getJdbcUserName();
    String password = server.getJdbcUserPassword();

    java.sql.Connection connection = DriverManager.getConnection(url, user, password);

    synchronized(connection)  {
      try {

        long uniqueId = getUniqueId(connection);

        if(uniqueId == 0) {
          throw new SQLException("Unable to create uniqe Id.");
        }

        pstmt = connection.prepareStatement(sqlString);
        pstmt.setLong(1, uniqueId);

        pstmt.setString(2, request.getFieldValue(Constants.USER_NAME_FIELD));
        pstmt.setString(3, fromStage);
        pstmt.setString(4, toStage);
        pstmt.setDate(5, new java.sql.Date(System.currentTimeMillis()));

        pstmt.setString(6, status);
        pstmt.setString(7, request.getFieldValue(Constants.DIRECTORY_NAME_FIELD));
        pstmt.setString(8, request.getFieldValue(Constants.FILE_NAME_FIELD));
        pstmt.setDate(9, new java.sql.Date(System.currentTimeMillis()));

        pstmt.setString(10, request.getFieldValue(Constants.TAG_FIELD));
        pstmt.setString(11, request.getFieldValue(Constants.COMMENT_FIELD));
        pstmt.setString(12, scriptName);
        pstmt.setString(13, returnedMessage);
        
        pstmt.executeUpdate();
        pstmt.close();
        pstmt = null;
        connection.commit();
      }
      catch (SQLException e) {
        e.printStackTrace();
        connection.rollback();
      }
      finally
      {
        if(pstmt != null) {
          pstmt.close();
        }

        if(connection != null) {
          connection.close();
        }
      }
    }
  }

  private long getUniqueId(java.sql.Connection connection) throws Exception {
    String sqlString = "SELECT TASK_SEQ.NEXTVAL FROM DUAL";

    Statement stmt = connection.createStatement();

    ResultSet rset = stmt.executeQuery(sqlString);

    rset.next();

    long id = rset.getLong("NEXTVAL");

    rset.close();
    
    return id;
  }

  /**
   * Performs the "get" command.
   *
   * @param  request  the request object
   * @param  response  the response object
   */
  private void doGet(InfoWorm request, InfoWorm response) {
    String fileName = request.getFieldValue(Constants.FILE_NAME_FIELD);

    try {
      File file = (new File(fileName)).getAbsoluteFile();

      if(!file.exists()) {
        doError(response, "File " + file + " doesn't exist.");
        return;
      }

      byte[] body = FileUtil.getFileAsBytes(file.getAbsolutePath());

      response.setBody(body);

      response.setField(Constants.DIRECTORY_NAME_FIELD,
                              file.getParent());
      response.setField(Constants.FILE_NAME_FIELD, file.getName());
      response.setField(Constants.CONTENT_LENGTH_FIELD,
                              String.valueOf(body.length));
      response.setField(Constants.STATUS_FIELD, Status.PROCESSED);

      server.getLogger().logMessage("Command: " + Command.GET + ".");
    }
    catch(IOException e) {
      doError(response, "" + e.toString());
      return;
    }
  }

  /**
   * Performs the "script" command.
   *
   * @param  request  the request object
   * @param  response  the response object
   */
  private void doScript(InfoWorm request, InfoWorm response) {
    String scriptName = request.getFieldValue(Constants.FILE_NAME_FIELD);

    List params = new ArrayList();

    params.add(scriptName);
    
    String scriptParams = new String(request.getBody());
    
    StringTokenizer st = new StringTokenizer(scriptParams);
    
    while(st.hasMoreTokens()) {
      params.add(st.nextToken());
    }

    try {
      int exitValue = executor.execute(params);

      if(exitValue == 0) {
        response.setField(Constants.STATUS_FIELD, Status.PROCESSED);
      }
      else {
        response.setField(Constants.STATUS_FIELD, Status.FAILED);
        response.setField(Constants.OS_ERROR_NUMBER_FIELD, 
                                String.valueOf(exitValue));
      }
      
      server.getLogger().logMessage("Command: " + Command.SCRIPT + ".");
    }
    catch(IOException e1) {
      doError(response, "Error during script execution : " + e1.toString());
      return;
    }
    catch(InterruptedException e2) {
      doError(response, "The script " + scriptName + " is not responding. Killed.");
      return;
    }
  }

  /**
   * Prepares the "error" response.
   *
   * @param  response  the response object
   * @param  message  the error message
   */
  private void doError(InfoWorm response, String message) {
    response.setField(Constants.ERROR_MESSAGE_FIELD, message);
    response.setField(Constants.STATUS_FIELD, Status.ERROR);

    server.getLogger().logMessage(message);
  }

  /**
   * Gets the stage for given directory
   * @param  dirName the directory name
   *
   * @return the stage
   */
  public String getStage(String dirName) {
    Map stages = server.getStages();

    Iterator iterator = stages.keySet().iterator();

    while(iterator.hasNext()) {
      String iStage = (String)iterator.next();

      String iDirName = (String)stages.get(iStage);

      if(dirName.startsWith(iDirName)) {
        return iStage;
      }
    }

    return null;
  }

  /**
   * Gets the name of the "to" stage.
   *
   * @param fromStage the name of the "from" stage
   * @return the name of the "to" stage, corresponding to "from" stage
   */
  private Association getAssociation(String fromStage, String extension) {
    List associations = server.getAssociations();

    for(int i = 0; i < associations.size(); i++) {
      Association association = (Association)associations.get(i);

      StagePair pair = association.getStagePair();
                          
      if(pair.getFromStage().equals(fromStage) &&
         association.getExtensions().contains(extension)) {
        return association;
      }
    }

    return null;
    
/*    List movements = server.getMovements();

    for(int i = 0; i < movements.size(); i++) {
      StagePair pair = (StagePair)movements.get(i);

      if(fromStage.equals(pair.getFromStage())) {
        return pair.getToStage();
      }
    }

    return null;
*/
  }

  /**
   * Gets the name of the script to be executed.
   *
   * @param stagePair  the pair of "from" and "to" stages
   * @param extension  the extension
   * @return  the name of the script
   */
/*  private String getScriptName(StagePair stagePair, String extension) {
    List associations = server.getAssociations();

    for(int i = 0; i < associations.size(); i++) {
      Association association = (Association)associations.get(i);

      if(association.equals(new Association(stagePair, extension))) {
        return association.getScriptName();
      }
    }

    return null;
  }
*/

}
