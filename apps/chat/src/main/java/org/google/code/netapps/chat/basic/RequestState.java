/*
 * @(#)RequestState.java 1.0 06/24/99
 *
 */

package org.google.code.netapps.chat.basic;

/**
 * The class contains constants for state of cutomer's request.
 *
 * @version 1.0 06/24/99
 * @author Alexander Shvets
 */
public interface RequestState {

  public String PENDING_STATE     = "pending";
  public String OPEN_STATE        = "open";
  public String ACCEPTED_STATE    = "accepted";
  public String ESCALATED_STATE   = "escalated";
  public String HOLD_STATE        = "hold";
  public String COMPLETED_STATE   = "completed";
  public String EXPIRED_STATE     = "expired";
  public String INACTIVATED_STATE = "inactivated";

}

