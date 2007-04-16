/*
 * @(#)PrivilegedParticipant.java 1.0 06/24/99
 *
 */

package org.google.code.netapps.chat.primitive;

/**
 * Basic class for privileged participants of chat conversation,
 * such as CSRs, experts and supervisors. Such participants have access
 * to some information database. Now they can access to document repsitory.
 * It is shared between all privileged participants.
 *
 * Privileged participant can change its own name.
 *
 * @version 1.0 06/24/99
 * @author Alexander Shvets
 */
public class PrivilegedParticipant extends Participant {

  /**
   * Constructs a privileged participant with the specified name.
   *
   * @param   name   the name of privileged participant
   */
  public PrivilegedParticipant(String name) {
    super(name);
  }

}
