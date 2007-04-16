// Context.java

package org.google.code.servant.net;

/**
 * This interface represents the context for registerd user.
 *
 * @version 1.0 08/14/2001
 * @author Alexander Shvets
 */
public interface Context {

  /**
   * Gets the last touch time
   *
   * @return  the last touch time
   */
  public long getTouchTime();

  /**
   * Touches the context
   */
  public void touch();

  /**
   * Checks if context is locked, i.e. temporarily cannot be checked by
   * context manager
   *
   * @return true if context is locked; false otherwise
   */
  public boolean isLocked();

  /**
   * Sets the locked boolean value
   *
   * @param locked the locked value
   */
  public void setLocked(boolean locked);

  /**
   * Cancels the context
   *
   */
  public void cancel();

}
