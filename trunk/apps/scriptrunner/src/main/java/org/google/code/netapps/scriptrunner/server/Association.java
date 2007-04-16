// Association.java

package org.google.code.netapps.scriptrunner.server;

import java.util.*;

/**
 * This class represents a simple data structure for holding associations
 * between stages, extension and script file that will be executed.
 *
 * @version 1.0 05/17/2001
 * @author Alexander Shvets
 */
public class Association {
  /** The pair of "from" and "to" stages */
  private StagePair stagePair;

  /** The list of extensions */
  private List extensions;

  /** The name of the script to be executed */
  private String scriptName;

  /**
   * Creates new association
   *
   * @param fromStage  the "from" stage
   * @param toStage  the "to" stage
   */
  public Association(String fromStage, String toStage) {
    this(new StagePair(fromStage, toStage), new ArrayList());
  }

  /**
   * Creates new association
   *
   * @param fromStage  the "from" stage
   * @param toStage  the "to" stage
   * @param extension the extension
   */
  public Association(String fromStage, String toStage, String extension) {
    this(new StagePair(fromStage, toStage), extension);
  }

  /**
   * Creates new association
   *
   * @param stagePair the [from, to] pair of stages
   * @param extension the extension
   */
  public Association(StagePair stagePair, String extension) {
    this(stagePair, new ArrayList());

    extensions.add(extension);
  }

  /**
   * Creates new association
   *
   * @param stagePair the [from, to] pair of stages
   * @param extensions the list of extensions
   */
  public Association(StagePair stagePair, List extensions) {
    this(stagePair, extensions, null);
  }

  /**
   * Creates new association
   *
   * @param stagePair the [from, to] pair of stages
   * @param extensions the list of extensions
   * @param scriptName the script name
   */
  public Association(StagePair stagePair, List extensions, String scriptName) {
    this.stagePair  = stagePair;
    this.extensions = extensions;
    this.scriptName = scriptName;
  }

  /**
   * Gets the pair of stages
   *
   * @return  the pair of stages
   */
  public StagePair getStagePair() {
    return stagePair;
  }

  /**
   * Gets the script name
   *
   * @return  the script name
   */
  public String getScriptName() {
    return scriptName.replace('/', java.io.File.separatorChar);
  }

  /**
   * Sets the script name
   *
   * @param scriptName  the script name
   */
  public void setScriptName(String scriptName) {
    this.scriptName = scriptName;
  }

  /**
   * Gets the list of extensions
   *
   * @return  the list of extensions
   */
   public List getExtensions() {
    return extensions;
  }

  /**
   * Compares two associations; they are equal if have the same "from"
   * and "to" stages; otherwise - unequal.
   *
   * @param object  the object for comparison
   * @return  true if two pairs are equals; false otherwise
   */
  public boolean equals(Object object) {
    if(object instanceof Association) {
      Association association = (Association)object;

      if(stagePair.equals(association.getStagePair())) {
        List eList = association.getExtensions();
        
        for(int i=0; i < extensions.size(); i++) {
          String extension = (String)extensions.get(i);

          if(eList.contains(extension)) {
            return true;
          }
        }

        return false;
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
    StringBuffer sb = new StringBuffer();

    sb.append("[");
    sb.append(stagePair.toString());
    sb.append(", ");

    sb.append("(");

    for(int i=0; i < extensions.size(); i++) {
      sb.append(extensions.get(i));

      if(i < extensions.size()-1) {
        sb.append(' ');
      }
    }

    sb.append(")");

    sb.append(", ");
    sb.append(scriptName);
    sb.append("]");

    return sb.toString();
  }

}
