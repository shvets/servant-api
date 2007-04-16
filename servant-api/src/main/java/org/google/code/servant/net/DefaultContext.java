// DefaultContext.java

package org.google.code.servant.net;

/**
 * This represents represents default implementation of the Context
 * interface.
 *
 * @version 1.0 08/14/2001
 * @author Alexander Shvets
 */
public class DefaultContext implements Context {
  /** The locked value */
  private boolean locked;

  /** The last touch time value */
  private long touchTime;

  /**
   * Creates new context
   */
  public DefaultContext() {
    locked = false;

    touch();
  }

  /**
   * Gets the last touch time
   *
   * @return  the last touch time
   */
  public long getTouchTime() {
    return touchTime;
  }

  /**
   * Touches the context
   */
  public void touch() {
    touchTime = System.currentTimeMillis();
  }

  /**
   * Checks if context is locked, i.e. temporarily cannot be checked by
   * context manager
   *
   * @return true if context is locked; false otherwise
   */
  public boolean isLocked() {
    return locked;
  }

  /**
   * Sets the locked boolean value
   *
   * @param locked the locked value
   */
  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  /**
   * Cancels the context
   *
   */
  public void cancel() {}

}
