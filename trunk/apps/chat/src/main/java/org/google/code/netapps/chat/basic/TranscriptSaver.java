/*
 * @(#)TranscriptSaver.java 1.0 09/05/2000
 *
 */

package org.google.code.netapps.chat.basic;

import org.google.code.netapps.chat.primitive.*;

/**
 * This interface describes a behavior of object that can save chat transcript.
 * Real system should implement this interface to have an ability to have
 * "hard" copy of chat conversation
 *
 * @version 1.0 09/05/2000
 * @author Alexander Shvets
 */
public interface TranscriptSaver {

  /**
   * Save chat conversation
   *
   * @param transcript  the transcript to be saved
   */
  public void save(Transcript transcript);

}
