/*
 * @(#)Interaction.java 1.0 09/05/2000
 *
 */

package org.google.code.netapps.chat.primitive;

import java.io.*;
import java.util.*;

/**
 * Class for presenting single interaction between cusromer and CSR.
 * A set of interactions compose a single session.
 *
 * @version 1.0 09/05/2000
 * @author Alexander Shvets
 */
public class Interaction implements Nameable, Serializable {

  static final long serialVersionUID = 6398581304440326365L;

  /** Session for this interaction */
  private Session session;

  /** Assosiated comment for this interaction */
  private String comment = new String("");

  /** Assosiated transcript for this interaction */
  private Transcript transcript = new Transcript();

  /** An owner of this interaction */
  private String owner = new String("");

  /** The name of interaction */
  protected String name;

  /**
  * Constructs an interaction with the specified name.
  *
  * @param   name   the name of interaction
  */
  public Interaction(String name) {
    this.name = name;
  }

  /**
   * Get name of interaction.
   *
   * @return  name of interaction
   */
  public String getName() {
    return name;
  }

  /**
   * Get qualified name of interaction.
   *
   * @return  qualified name of interaction
   */
  public String getQualifiedName() {
    return "interaction" + " " + name;
  }

  /**
   * Get comment for this interaction.
   *
   * @return  comment for this interaction
   */
  public String getComment() {
    return comment;
  }

  /**
   * Set comment for this interaction.
   *
   * @param name  comment for this interaction
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * Get the owner of this interaction.
   *
   * @return owner of this interaction.
   */
  public String getOwner() {
    return owner;
  }

  /**
   * Set the owner of this interaction.
   *
   * @param owner  owner of this interaction.
   */
  public void setOwner(String owner) {
    this.owner = owner;
  }

  /**
   * Get the transcript of this interaction.
   *
   * @return transcript of this interaction.
   */
  public Transcript getTranscript() {
    return transcript;
  }

  /**
   * Get the session for this interaction.
   *
   * @return session for this interaction
   */
  public Session getSession() {
    return session;
  }

  /**
   * Set the session for this interaction.
   *
   * @param session for this interaction
   */
  public void setSession(Session session) {
    this.session = session;
  }

  /**
   * Compares two Objects for equality. Two interactions will be equal
   *          if they both have the same name.
   *
   * @param   object  the reference object with which to compare.
   * @return  true if this object is the same as the obj
   *          argument; false otherwise.
   */
  public boolean equals(Object object) {
    if(object instanceof String) {
      String name = (String)object;
      return this.name.equals(name);
    }
    else if(object instanceof Interaction) {
      Interaction interaction = (Interaction)object;
      return this.name.equals(interaction.name);
    }

    return false;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return  a string representation of the object.
   */
  public String toString() {
    return getQualifiedName();
  }


  public static void main(String[] args) throws Exception {
    Interaction interaction1 = new Interaction("interaction1");
    System.out.println("Interaction interaction1 : " + interaction1);

    Transcript t1 = interaction1.getTranscript();

    t1.append("Hello, friend!");
    t1.append("line1");
    t1.append("line2");
    t1.append("line3");
    t1.append("end.");

    interaction1.setComment("No matter");

    System.out.println("Print interaction1:\n");
    System.out.println("Comment: " + interaction1.getComment());
    System.out.println();
    System.out.println("Report: ");

    Vector verbatim = t1.getVerbatim();
    for(int i=0; i < verbatim.size(); i++) {
      System.out.println(verbatim.elementAt(i));
    }
  }

}