// Constants.java

package org.google.code.netapps.scriptrunner;

/**
 * This interface is a wrapper for definitions of header fields that will
 * be used for carrying information between a client and a server.
 *
 * @version 1.0 05/16/2001
 * @author Alexander Shvets
 */
public interface Constants {

  public String CONTENT_LENGTH_FIELD  = "Content-Length";
  public String COMMAND_FIELD         = "Command";
  public String FILE_NAME_FIELD       = "File-Name";
  public String DIRECTORY_NAME_FIELD  = "Directory-Name";
  public String USER_NAME_FIELD       = "User-Name";
  public String FROM_STAGE_FIELD      = "From-Stage";
  public String TO_STAGE_FIELD        = "To-Stage";
  public String ERROR_MESSAGE_FIELD   = "Error-Message";
  public String OS_ERROR_NUMBER_FIELD = "Os-Error-Number";
  public String STATUS_FIELD          = "Status";
  public String TAG_FIELD             = "Tag";
  public String COMMENT_FIELD         = "Comment";

}
