/*
 * @(#)Expert.java 1.0 06/24/99
 *
 */

package org.google.code.netapps.chat.primitive;

/**
 * Class for presenting expert (privileged participant).
 *
 * @version 1.0 06/24/99
 * @author Alexander Shvets
 */
public class Expert extends PrivilegedParticipant {

  /**
   * Constructs expert with the specified name.
   *
   * @param   name   the name of expert
   */
  public Expert(String name) {
    super(name);
  }

  /**
   * Get type.
   *
   * @return  type
   */
  public String getType() {
    return ParticipantType.EXPERT;
  }

}
