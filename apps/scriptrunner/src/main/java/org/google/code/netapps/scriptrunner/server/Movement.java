// Movement.java

package org.google.code.netapps.scriptrunner.server;


/**
 * This class represents a simple data structure for holding associations
 * between stages, extension and script file that will be executed.
 *
 * @version 1.0 05/17/2001
 * @author Alexander Shvets
 */
public class Movement extends StagePair {
  private String backStage;

  /**
   * Creates new association
   *
   * @param fromStage the "from" stage
   * @param toStage the "to" stage
   * @param backStage the "back" stage
   */
  public Movement(String fromStage, String toStage, String backStage) {
    super(fromStage, toStage);

    this.backStage = backStage;
  }

  /**
   * Gets the "back" stage
   *
   * @return  the "back" stage
   */
  public String getBackStage() {
    return backStage;
  }

  /**
   * Compares two stage pairs; they are equal if have the same "from",
   * "to" and "back" stages; otherwise - unequal.
   *
   * @param object  the object for comparison
   * @return  true if two pairs are equals; false otherwise
   */
  public boolean equals(Object object) {
    if(super.equals(object)) {
      if(object instanceof Movement) {
        Movement movement = (Movement)object;

        if(backStage.equals(movement.getBackStage())) {
          return true;
        }
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
    return "(" + fromStage + ", " + toStage + ", " + backStage + ")";
  }

}
