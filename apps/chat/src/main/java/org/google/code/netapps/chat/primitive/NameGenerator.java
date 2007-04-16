/*
 * @(#)NameGenerator.java 1.0 07/05/99
 *
 */

package org.google.code.netapps.chat.primitive;

/**
 * Class for generating simple names on base of incremental counter.
 *
 * @version 1.0 07/05/99
 * @author Alexander Shvets
*/
public class NameGenerator implements java.io.Serializable {

  static final long serialVersionUID = -2550202272594964528L;

  /** A counter which number will be used for name construction */
  private long cnt = 0;

  /** A name prefix */
  protected String prefix = "";

  /**
   * Creates name generator with default prefix
   */
  public NameGenerator() {}

  /**
   * Creates name generator with spesified prefix
   */
  public NameGenerator(String prefix) {
    this.prefix = prefix;
  }

  /**
   * Get new name from name generator
   */
  public String getNewName() {
    return prefix + Long.toString(++cnt);
  }

  /**
   * Release specified name to be reused
   *
   * @param name  the name that can be reused after release
   */
  public void release(String name) {}

}
