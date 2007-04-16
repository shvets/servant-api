/*
 * @(#)CSR.java 1.0 06/24/99
 *
 */

package org.google.code.netapps.chat.primitive;

/**
 * Class for presenting CSR (privileged participant).
 *
 * @version 1.0 06/24/99
 * @author Alexander Shvets
 */
public class CSR extends PrivilegedParticipant {

  /**
   * Constructs CRS with the specified name.
   *
   * @param   name   the name of CSR
   */
  public CSR(String name) {
    super(name);
  }

  /**
   * Get type.
   *
   * @return  type
   */
  public String getType() {
    return ParticipantType.CSR;
  }

}
