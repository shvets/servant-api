// Server.java

package org.google.code.servant.net;

import org.google.code.servant.util.Logger;

/**
 * Server's common behavior
 *
 * @version 1.1 08/09/2001
 * @author Alexander Shvets
 */
public interface Server {

  /**
   * Starts the server
   *
   * @exception  Exception if exception occurs.
   */
  public void start() throws Exception;

  /**
   * Stops the server
   *
   * @exception  Exception if exception occurs.
   */
  public void stop() throws Exception;

  /**
   * Gets the connection factory
   *
   * @return the connection factory
   */
  public ConnectionFactory getConnectionFactory();

  /**
   * Sets the connection factory
   *
   * @param connectionFactory the connection factory
   */
  public void setConnectionFactory(ConnectionFactory connectionFactory);

  /**
   * Gets the servant manager
   *
   * @return the servant manager
   */
  public ServantManager getServantManager();

  /**
   * Sets the servant manager
   *
   * @param servantManager the servant manager
   */
  public void setServantManager(ServantManager servantManager);

  /**
   * Gets the context manager
   *
   * @return the context manager
   */
  public ContextManager getContextManager();

  /**
   * Sets the context manager
   *
   * @param contextManager the context manager
   */
  public void setContextManager(ContextManager contextManager);

  /**
   * Gets the logger object
   *
   * @return  the logger object
   */
  public Logger getLogger();

  /**
   * Sets the logger object
   *
   * @param logger  the logger object
   */
  public void setLogger(Logger logger);

}
