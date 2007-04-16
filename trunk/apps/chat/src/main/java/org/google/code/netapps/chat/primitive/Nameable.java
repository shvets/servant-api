/*
 * @(#)Nameable.java 1.0 06/24/99
 *
 */

package org.google.code.netapps.chat.primitive;

/**
 * The ability to have some name.
 *
 * @version 1.0 06/24/99
 * @author Alexander Shvets
 */
public interface Nameable {

  /**
   * Get name of object.
   *
   * @return name of object as a string
   */
  public String getName();

  /**
   * Get qualified name of object.
   *
   * @return qualified name of object as a string
   */
  public String getQualifiedName();

}