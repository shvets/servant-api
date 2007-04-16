// ScriptExecutor.java

package org.google.code.servant.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class wraps all work related to script execution.
 * It is implemented as Singleton pattern.
 *
 * @version 1.0 05/18/2001
 * @author Alexander Shvets
 */
public class ScriptExecutor {
  private StringBuffer stdOutput = new StringBuffer();
  private StringBuffer errOutput = new StringBuffer();

  /** The name of the user */
  private String userName;

  /**
   * Creates an instance of this class.
   */
  public ScriptExecutor() {}

  /**
   * Gets the user name.
   *
   * @return  the name of the user
   */
  public String  getUserName() {
    return userName;
  }

  /**
   * Sets the user name.
   *
   * @param userName   the name of the user
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Executes the script with one parameter.
   *
   * @param scriptName the name of the script
   * @param param the single parameter-string
   * @return  the status of execution
   * @exception  InterruptedException  if an interruption occurs.
   * @throws IOException I/O exception
   */
  public int execute(String scriptName, String param)
             throws IOException, InterruptedException {
    List params = new ArrayList();

    params.add(scriptName);
    params.add(param);

    return execute(params);
  }

  /**
   * Executes the command outlined in params list.
   *
   * @param params the list of parameters
   * @return  the exit value
   * @exception  IOException  if an I/O error occurs.
   * @exception  InterruptedException  if an interruption occurs.
   */
  public int execute(List params) throws IOException, InterruptedException {
    stdOutput.delete(0, stdOutput.length());
    errOutput.delete(0, errOutput.length());

    String osName = System.getProperties().getProperty("os.name");

    String[] args;

    int offset = 0;

    if(osName.startsWith("Windows")) {
      args = new String[params.size()+2];

      args[offset++] = "command.com";
      args[offset++] = "/C";
    }
    else {
      args = new String[params.size()];
    }

    for(int i=0; i < params.size(); i++) {
      args[i+offset] = (String)params.get(i);
    }

    Process process = Runtime.getRuntime().exec(args);

    process.waitFor();

    redirectStream(process.getInputStream(), stdOutput);
    redirectStream(process.getErrorStream(), errOutput);

    return process.exitValue();
  }

  /**
   * Redirects the stream to a console.
   *
   * @param is  the input stream
   * @param sb  the string buffer
   * @throws IOException I/O exception
   */
  private void redirectStream(InputStream is, StringBuffer sb)
               throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

    while(true) {
      String line = reader.readLine();

      if(line == null) {
        break;
      }

      sb.append(line).append("\r\n");
    }

    reader.close();
  }

  public String getStandardOutput() {
    return stdOutput.toString();
  }

  public String getErrorOutput() {
    return errOutput.toString();
  }

}
