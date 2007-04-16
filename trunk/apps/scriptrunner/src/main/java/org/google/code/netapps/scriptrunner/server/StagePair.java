// StagePair.java

package org.google.code.netapps.scriptrunner.server;

/**
 * This class represents a simple data structure for holding associations
 * between stages, extension and script file that will be executed.
 *
 * @version 1.0 05/17/2001
 * @author Alexander Shvets
 */
public class StagePair {
  protected String fromStage;
  protected String toStage;

  /**
   * Creates new association
   *
   * @param fromStage the "from" stage
   * @param toStage the "to" stage
   */
  public StagePair(String fromStage, String toStage) {
    this.fromStage = fromStage;
    this.toStage   = toStage;
  }

  /**
   * Gets the "from" stage
   *
   * @return  the "from" stage
   */
  public String getFromStage() {
    return fromStage;
  }

  /**
   * Gets the "to" stage
   *
   * @return  the "to" stage
   */
  public String getToStage() {
    return toStage;
  }

  /**
   * Compares two stage pairs; they are equal if have the same "from"
   * and "to" stages; otherwise - unequal.
   *
   * @param object  the object for comparison
   * @return  true if two pairs are equals; false otherwise
   */
  public boolean equals(Object object) {
    if(object instanceof StagePair) {
      StagePair pair = (StagePair)object;

      if(fromStage.equals(pair.getFromStage()) && toStage.equals(pair.getToStage())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Gets the string representation of object.
   *
   * @return  the string representation of object
   */
  public String toString() {
    return "(" + fromStage + ", " + toStage + ")";
  }

}
