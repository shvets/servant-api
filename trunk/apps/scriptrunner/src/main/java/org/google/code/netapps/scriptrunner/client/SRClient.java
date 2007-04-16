// SRClient.java

package org.google.code.netapps.scriptrunner.client;

import java.io.*;
import java.util.*;

import org.google.code.servant.net.infoworm.InfoWormClient;
import org.google.code.servant.net.infoworm.InfoWorm;
import org.google.code.netapps.scriptrunner.Status;
import org.google.code.netapps.scriptrunner.Constants;
import org.google.code.netapps.scriptrunner.Command;


/**
 * This class represents a client for Script Runner server.
 *
 * @version 1.0 05/16/2001
 * @author Alexander Shvets
 */
public class SRClient extends InfoWormClient {
  /** The name of the user */
  private String userName;

  /**
   * Creates a new instance of client.
   */
  public SRClient() {
    userName = System.getProperties().getProperty("user.name");
  }

  /**
   * Creates a new instance of client.
   *
   * @param userName  the name of the user
   */
  public SRClient(String userName) {
    this.userName = userName;
  }

  /**
   * Gets the name of the user
   *
   * @return  the name of the user
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Shows the status of response. In case of error indicates it by
   * showing error message and error code.
   *
   * @param response  the response object
   * @return true, if command was processed successfully; false otherwise
   */
  private boolean showStatus(InfoWorm response) {
    String status = response.getFieldValue(Constants.STATUS_FIELD);

    System.out.println("Status   : " + status + ".");

    if(!status.equals(Status.PROCESSED)) {
      String errorMessage = response.getFieldValue(Constants.ERROR_MESSAGE_FIELD);

      if(errorMessage != null) {
        System.out.println("Error message: " + errorMessage + ".");
      }

      String exitCode = response.getFieldValue(Constants.OS_ERROR_NUMBER_FIELD);

      if(exitCode != null) {
        System.out.println("Exit code: " + exitCode + ".");
      }

      System.out.println("Error stream from server: ");
      System.out.println(new String(response.getBody()));

      return false;
    }

    System.out.println("Output stream from server: ");
    System.out.println(new String(response.getBody()));
      
    return true;  
  }

  /**
   * Promotes a file to the next stage
   *
   * @param params  the list of parameters for this command
   * @exception  IOException  if an I/O error occurs.
   */
  public void promoteFile(List params) throws IOException {
    if(params.size() == 0) {
      System.out.println("Command \"" + Command.PROMOTE + "\"" +
                         " should have \"fileName\" parameter.");
      return;
    }
    
    String fileName = (String)params.get(0);

    File file = (new File(fileName)).getAbsoluteFile();

    if(!file.exists()) {
      System.out.println("File " + fileName + " doesn't exist.");
      return;
    }

    InfoWorm request = new InfoWorm();

    if(params.size() > 1) {
      request.setField(Constants.TAG_FIELD, (String)params.get(1));
    }

    if(params.size() > 2) {
      request.setField(Constants.COMMENT_FIELD, (String)params.get(2));
    }

    request.setField(Constants.USER_NAME_FIELD, userName);
    request.setField(Constants.COMMAND_FIELD, Command.PROMOTE);

    request.setField(Constants.DIRECTORY_NAME_FIELD, file.getParent());
    request.setField(Constants.FILE_NAME_FIELD, file.getName());

/*    byte body[] = FileUtil.getFileAsBytes(fileName);

    request.setField(Constants.CONTENT_LENGTH_FIELD, String.valueOf(body.length));
    request.setBody(body);
*/
    Object[] responses = pipe(request);

    for(int i=0; i < responses.length; i++) {
      InfoWorm response = (InfoWorm)responses[i];

      if(showStatus(response)) { 
        String fromStage = response.getFieldValue(Constants.FROM_STAGE_FIELD);
        String toStage = response.getFieldValue(Constants.TO_STAGE_FIELD);

        System.out.println("From     : " + fromStage + ".");
        System.out.println("To       : " + toStage + ".");
        System.out.println("File name: " + fileName + ".");
      }
    }
  }

  /**
   * Gets the file from specified stage
   *
   * @param params  the list of parameters for this command
   * @exception  IOException  if an I/O error occurs.
   */
  public void getFile(List params) throws IOException {
    if(params.size() == 0) {
      System.out.println("Command \"" + Command.GET + "\"" +
                         " should have \"fileName\" parameter.");
      return;
    }
    
    String fileName = (String)params.get(0);

    InfoWorm request = new InfoWorm();

    request.setField(Constants.USER_NAME_FIELD, userName);
    request.setField(Constants.COMMAND_FIELD, Command.GET);
//    request.setField("Directory-Name", "/export/home/ashvets/");
    request.setField(Constants.FILE_NAME_FIELD, fileName);

//    InfoWorm response = (InfoWorm)pipe(request);
    Object[] responses = pipe(request);

    for(int i=0; i < responses.length; i++) {
      InfoWorm response = (InfoWorm)responses[i];

      if(showStatus(response)) { 
        String dirName = response.getFieldValue(Constants.DIRECTORY_NAME_FIELD);

        byte body[] = response.getBody();

        FileOutputStream fos = 
            new FileOutputStream(/*dirName.replace('/', File.separator) + */
                                  "scripts" + File.separator + fileName);
        fos.write(body);

        fos.close();

        System.out.println("Got file: " + fileName + ".");
      }
    }
  }

  /**
   * Executes script
   *
   * @param params  the list of parameters for this command
   * @exception  IOException  if an I/O error occurs.
   */
  public void executeScript(List params) throws IOException {
    if(params.size() == 0) {
      System.out.println("Command \"" + Command.SCRIPT+ "\"" +
                         " should have \"scriptName\" parameter.");
      return;
    }
    
    String scriptName = (String)params.get(0);

    StringBuffer scriptParams = new StringBuffer();

    for(int i=0; i < params.size(); i++) {
      scriptParams.append((String)params.get(i));
      
      if(i < params.size()-1) {
        scriptParams.append(' ');
      }
    }

    InfoWorm request = new InfoWorm();

    request.setField(Constants.USER_NAME_FIELD, userName);
    request.setField(Constants.COMMAND_FIELD, Command.SCRIPT);
    request.setField(Constants.FILE_NAME_FIELD, scriptName);

    byte[] body = scriptParams.toString().getBytes();

    request.setField(Constants.CONTENT_LENGTH_FIELD, String.valueOf(body.length));
    request.setBody(body);

//    InfoWorm response = (InfoWorm)pipe(request);

    Object[] responses = pipe(request);

    for(int i=0; i < responses.length; i++) {
      InfoWorm response = (InfoWorm)responses[i];

      if(showStatus(response)) { 
        System.out.println("Script " + scriptName + " executed.");
      }
    }
  }

  /**
   * Executes the command as is
   *
   * @param params  the list of parameters for this command
   * @exception  IOException  if an I/O error occurs.
   */

  public void executeAsIs(String command, List params) throws IOException {
    InfoWorm request = new InfoWorm();

    StringBuffer commandParams = new StringBuffer();

    for(int i=0; i < params.size(); i++) {
      commandParams.append((String)params.get(i));
      
      if(i < params.size()-1) {
        commandParams.append(' ');
      }
    }

    request.setField(Constants.USER_NAME_FIELD, userName);
    request.setField(Constants.COMMAND_FIELD, command);

    byte[] body = commandParams.toString().getBytes();

    request.setField(Constants.CONTENT_LENGTH_FIELD, String.valueOf(body.length));
    request.setBody(body);

//    InfoWorm response = (InfoWorm)pipe(request);
    Object[] responses = pipe(request);

    for(int i=0; i < responses.length; i++) {
      InfoWorm response = (InfoWorm)responses[i];

      if(showStatus(response)) { 
        System.out.println("Command " + command + " executed.");
      }
    }
  }
 
  private static String readLine(String message) {
    System.out.print(message + ": ");

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    String line = "";

    try {
      line = reader.readLine();
    }
    catch(IOException e) {
      e.printStackTrace();
    }

    return line;
  }

  /**
   * The main entry to a client program.
   */
  public static void main(String argv[]) throws IOException {
    String command = null;
    
    List params = new ArrayList();

    String host = "127.0.0.1";
    int port = 8181;

    for(int i=0; i < argv.length; ) {
      String arg = argv[i++];
  
      if(arg.equals("-host")) {
        host = argv[i++];
      }
      else if(arg.equals("-port")) {
        port = Integer.parseInt(argv[i++]);
      }
      else {
        params.add(arg);
      }
    }

    try {
      command = ((String)params.get(0)).toLowerCase();

      params.remove(0);
    }
    catch(ArrayIndexOutOfBoundsException e) {
      System.out.println("Usage: SRClient [-host host] [-port port] <command> [params...]");
      System.out.println();
      System.out.println("where command is [promote, get, script]");
      System.out.println("      params are parameters for command.");

      return;
    }

    SRClient client = new SRClient();

    client.setHost(host);
    client.setPort(port);

    System.out.println();
    System.out.println("User name: " + client.getUserName());
    System.out.println("Command  : " + command);
    System.out.println();

    if(command.equals(Command.PROMOTE)) {
      params.add(/*readLine("Input tag")*/"tag");
      params.add(readLine("Input comments"));

      client.promoteFile(params);
    }
    else if(command.equals(Command.GET)) {
      client.getFile(params);
    }
    else if(command.equals(Command.SCRIPT)) {
      client.executeScript(params);
    }
    else {
      client.executeAsIs(command, params);
    }

  }

}
