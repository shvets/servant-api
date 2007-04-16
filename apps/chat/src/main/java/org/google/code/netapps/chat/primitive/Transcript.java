/*
 * @(#)Transcript.java 1.0 08/30/00
 *
 */

package org.google.code.netapps.chat.primitive;

import java.io.*;
import java.util.*;

/**
 * This class represents the entire content of chat conversation.
 *
 * @version 1.0 08/30/00
 * @author Alexander Shvets
 */
public class Transcript implements Serializable {

  static final long serialVersionUID = -5111503119893513894L;

  /** The size of transcript */
  private int size = 0;

  /** Holder of transcript content */
  private Vector verbatim = new Vector();

  /**
   * Append new line to transcript.
   *
   * @param   line   string to be appended to transcript
   */
  public void append(String line) {
    verbatim.addElement(line);

    size += (line.length() + 2);
  }

  /**
   * Get the verbatim.
   *
   * @return verbatim
   */
  public Vector getVerbatim() {
    return verbatim;
  }

  /**
   * Get the size of transcript.
   *
   * @return  the size of transcript
   */
  public int size() {
    return size;
  }

  /**
   * Compares two Objects for equality. Two interactions will be equal
   *          if they both have the same verbatim.
   *
   * @param   object  the reference object with which to compare.
   * @return  true if this object is the same as the obj
   *          argument; false otherwise.
   */
  public boolean equals(Object object) {
    if(object instanceof Transcript) {
      Transcript transcript = (Transcript)object;
      return this.verbatim.equals(transcript.verbatim);
    }

    return false;
  }

}
