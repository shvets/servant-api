/*
 * @(#)Supervisor.java 1.0 06/24/99
 *
 */

package org.google.code.netapps.chat.primitive;

/**
 * Class for presenting supervisor (privileged participant).
 *
 * @version 1.0 06/24/99
 * @author Alexander Shvets
 */
public class Supervisor extends PrivilegedParticipant {

  /**
  * Constructs supervisor with the specified name.
  *
  * @param   name   the name of supervisor
  */
  public Supervisor(String name) {
    super(name);
  }

  /**
   * Get type.
   *
   * @return  type
   */
  public String getType() {
    return ParticipantType.SUPERVISOR;
  }

}
