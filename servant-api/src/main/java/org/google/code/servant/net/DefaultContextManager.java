// DefaultContextManager.java

package org.google.code.servant.net;

import java.util.Map;
import java.util.HashMap;

/**
 * This is a default implementation of ContextManager interface.
 *
 * @version 1.0 08/14/2001
 * @author Alexander Shvets
 */
public class DefaultContextManager implements ContextManager {
  /** The map of contexts */
  protected Map contexts = new HashMap();

  /**
   * Puts new context into management
   *
   * @param name the name of the context
   * @param context the context
   */
  public void put(String name, Context context) {
    contexts.put(name, context);
  }

  /**
   * Gets the context
   *
   * @param name the name of the context
   * @return the context
   */
  public Context get(String name) {
    return (Context)contexts.get(name);
  }

  /**
   * Removes the context from management
   *
   * @param name the name of the context
   */
  public void remove(String name) {
    contexts.remove(name);
  }

  /**
   * Gets all contexts under management
   *
   * @return all contexts under management
   */
  public Map getContexts() {
    return contexts;
  }

}
