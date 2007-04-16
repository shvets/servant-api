// ContextManager.java

package org.google.code.servant.net;

import java.util.Map;

/**
 * This interface represents the manager that controls how contexts
 * will be appeared and disappeared.
 *
 * @version 1.0 08/14/2001
 * @author Alexander Shvets
 */
public interface ContextManager {

  /**
   * Puts new context into management
   *
   * @param name the name of the context
   * @param context the context
   */
  public void put(String name, Context context);

  /**
   * Gets the context
   *
   * @param name the name of the context
   * @return the context
   */
  public Context get(String name);

  /**
   * Removes the context from management
   *
   * @param name the name of the context
   */
  public void remove(String name);

  /**
   * Gets all contexts under management
   *
   * @return all contexts under management
   */
  public Map getContexts();

}
