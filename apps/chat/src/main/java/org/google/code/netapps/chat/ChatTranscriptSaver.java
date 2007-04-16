/*
 * @(#)ChatTranscriptSaver.java 1.0 09/05/2000
 *
 */

package org.google.code.netapps.chat;

import java.util.*;

import org.google.code.netapps.chat.basic.*;
import org.google.code.netapps.chat.primitive.*;

/**
 *
 * @author Alexander Shvets
 * @version 1.0 09/05/2000
 */
public class ChatTranscriptSaver implements TranscriptSaver {

  /**
   * Save chat conversation
   *
   * @param transcript  the transcript to be saved
   */
  public void save(Transcript transcript) {
    Vector verbatim = transcript.getVerbatim();

    for(int i=0; i < verbatim.size(); i++) {
      System.out.println(">>> " + verbatim.elementAt(i));
    }
  }

}
