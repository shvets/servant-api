/*
 * @(#)Session.java 1.0 09/06/99
 *
 */

package org.google.code.netapps.chat.primitive;

import java.io.*;
import java.util.*;

/**
 * Class for presenting all interactions between cusromer
 * and store personnel.
 *
 * @version 1.0 07/07/99
 * @author Alexander Shvets
 */
public class Session implements Nameable, Serializable {

  static final long serialVersionUID = 6790920151413524907L;

  /** A directory in which all sessions will be saved (in serialized form) */
  private static String sessionDir = "sessions";

  /** Name generator for interactions */
  private NameGenerator interactionNameGen = new NameGenerator("i");

  /** Last used interaction */
  private Interaction lastIinteraction;

  /** Session name */
  protected String name;

  /**
  * Constructs a session with the specified name.
  *
  * @param   name   the name of session
  */
  public Session(String name) {
    this.name = name;
  }

  /**
   * Get name of session.
   *
   * @return  name of session
   */
  public String getName() {
    return name;
  }

  /**
   * Get qualified name of session.
   *
   * @return  qualified name of session
   */
  public String getQualifiedName() {
    return "session" + " " + name;
  }

  /**
   * Set up directory for this session.
   *
   * @param  sd  the directory for this session
   */
  public static void setDirectory(String sd) {
    sessionDir = sd;
  }

  /**
   * Get directory for this session.
   *
   * @return   the directory for this session
   */
  public String getDirectory() {
    return sessionDir;
  }

  /**
   * Creates new interaction. It becomes current interaction for this
   * session.
   *
   * @return   newly created interaction.
   * @exception  IOException  if an I/O error occurs.
   */
  public Interaction newInteraction() throws IOException {
    save();

    Interaction interaction = new Interaction(interactionNameGen.getNewName());
    interaction.setSession(this);

    lastIinteraction = interaction;

    return interaction;
  }

  /**
   * Saves current interaction.
   */
  public void save() throws IOException {
    if(lastIinteraction != null) {
      File dir = new File(sessionDir + File.separator + name);
      if(!dir.exists()) dir.mkdirs();

      FileOutputStream fos =  new FileOutputStream(
          sessionDir + File.separator + name +
          File.separator + lastIinteraction.getName());
      ObjectOutputStream oos = new ObjectOutputStream(fos);

      oos.writeObject(lastIinteraction);
      oos.flush();
      oos.close();
    }

    lastIinteraction = null;
  }

  /**
   * Physical deletion of the sessions
   * (directory subtree that corresponds this session).
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void delete() throws IOException {
    deleteDir(sessionDir, name);
  }

  /** Delete directory tree */
  private void deleteDir(String dirName, String fileName) {
    String newName = dirName + File.separator + fileName;
    File file = new File(newName);
    if(file.isDirectory()) {
      String[] list = file.list();
      for(int i=0; i < list.length; i++) {
        deleteDir(newName, list[i]);
      }
    }

    if(file.exists()) {
      file.delete();
    }
  }

  /**
   * Physical deletion of the interaction
   * (file that corresponds specified interaction).
   *
   * @exception  IOException  if an I/O error occurs.
   */
  public void delete(String interactionName) throws IOException {
    File file = new File(sessionDir + File.separator + name +
                         File.separator + interactionName);

    if(file.exists()) {
      file.delete();
    }
  }

  /**
   * Gets an interaction, specified by its name.
   *
   * @param  interactionName name of interaction to seek.
   * @return  founded interaction.
   * @exception  IOException  if an I/O error occurs.
   */
  public Interaction getInteraction(String interactionName) throws IOException {
    FileInputStream fis =
        new FileInputStream(sessionDir + File.separator +
                            getName() + File.separator + interactionName);
    ObjectInputStream ois = new ObjectInputStream(fis);
    Interaction interaction = null;
    try {
      interaction = (Interaction)ois.readObject();
    }
    catch(ClassNotFoundException e) {
      throw new IOException(e.toString());
    }
    finally {
      ois.close();
    }

    return interaction;
  }

  /**
   * Gets all sessions.
   *
   * @return  array of names of all created sessions.
   */
  public static String[] getSessions() {
    File sessionsDir = new File(sessionDir);
    if(sessionsDir.isDirectory())
      return sessionsDir.list();

    return null;
  }

  /**
   * Compares two Objects for equality. Two sessions will be equal
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
    else if(object instanceof Session) {
      Session session = (Session)object;
      return this.name.equals(session.name);
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
    Session session = new Session("session");
    System.out.println("Session session: " + session);

    Interaction interaction = session.newInteraction();
    String name1 = interaction.getName();
    System.out.println("Interaction interaction: " + interaction);

    Transcript t = interaction.getTranscript();

    t.append("Hello, friend!");
    t.append("line1");
    t.append("line2");
    t.append("line3");
    t.append("end.");

    interaction.setComment("No matter");

    session.save();

    interaction = session.newInteraction();
    System.out.println("Interaction interaction: " + interaction);

    t = interaction.getTranscript();

    t.append("Hello, anton!");
    t.append("bye.");

    interaction.setComment("No comments");

    session.save();

    Interaction interaction11 = session.getInteraction(name1);
    Transcript t11 = interaction11.getTranscript();

    System.out.println("Print interaction11:\n");
    System.out.println("Comment: " + interaction11.getComment());
    System.out.println();
    System.out.println("Verbatim: ");
    Vector verbatim = t11.getVerbatim();
    for(int i=0; i < verbatim.size(); i++) {
      System.out.println(verbatim.elementAt(i));
    }

    System.out.println("Existing sessions:\n");
    String[] sessions = Session.getSessions();
    for(int i=0; i < sessions.length; i++) {
      System.out.println(sessions[i]);
    }
  }

}
