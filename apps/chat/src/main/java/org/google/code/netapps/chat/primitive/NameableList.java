/*
 * @(#)NameableList.java 1.0 06/24/99
 *
 */

package org.google.code.netapps.chat.primitive;

import java.util.*;

/**
 * This class represent a container for objects that have the names.
 * By default, this list can hold objects of only one type, and this 
 * type should be specified during of a list creation.
 *
 * @version 1.0 06/24/99
 * @author Alexander Shvets
 */
public class NameableList implements java.io.Serializable {

  static final long serialVersionUID = -4266826441499419833L;
      
  /** The list of Nameable objects */
  protected Vector names;

  /** The type which is supported with this list */
  protected Class clazz;

  /**
   * Constructs an empty list with the specified type of object it can hold.
   *
   * @param   clazz     the type of object, that this list can hold
   */
  public NameableList(Class clazz) {
    this.clazz = clazz;

    names = new Vector();
  }

  /**
   * Returns the number of components in this list.
   *
   * @return  the number of components in this list.
   */
  public final int size() {
    return names.size();
  }

  /**
   * Adds the specified component to the end of this list.
   * If user try to add object with a type, different from type, that
   * suppors this list, a runtime exception will occur.
   *
   * @param   nameable   the component to be added.
   * @exception  IllegalArgumentException if an user try to add object with
   *             illegal type
   */
  public void add(Nameable nameable) {
    if(!nameable.getClass().equals(clazz))
      throw new IllegalArgumentException(
            "Component with type " +
            "\"" + nameable.getClass().getName() + "\"" +
            " cannot be added to container that support only type " +
            "\"" + clazz.getName() + "\"");

    if(!names.contains(nameable))
      names.addElement(nameable);
  }

  /**
   * Removes the first occurrence of the argument from this list.
   *
   * @param   obj   the component to be removed.
   * @return  true if the argument was a component of this
   *          vector; false otherwise.
   */
  public boolean remove(Nameable nameable) {
    return names.removeElement(nameable);
  }

  /**
   * Find the component in this list by the name.
   *
   * @param   name   the name of desired component.
   * @return  the component if it is inside this list; othewise - null
   */
  public Nameable getElement(String name) {
    synchronized(names) {
      for(int i=0; i < names.size(); ++i) {
        Nameable nameable = (Nameable)names.elementAt(i);
        if(nameable.equals(name))
          return nameable;
      }
    }

    return null;
  }

  /**
   * Clears the list with names.
   */
  public void clear() {
    names.removeAllElements();
  }

  /**
   * Tests if the specified object is a component in this list.
   *
   * @param   nameable   an component to be tested.
   * @return  true if the specified object is a component in
   *          this list; false otherwise.
   */
  public boolean contains(Nameable nameable) {
    synchronized(names) {
      for(int i=0; i < names.size(); ++i) {
        Nameable n = (Nameable)names.elementAt(i);
        if(n.getQualifiedName().equals(nameable.getQualifiedName()))
          return true;
      }
    }

    return false;
  }

  /**
   * Get names of all of objects in this container.
   *
   * @return names of all of objects in this container
   */
  public String[] getNames() {
    int sz = names.size();
    String[] result = new String[sz];
    for(int i=0; i < sz; ++i) {
      Nameable n = (Nameable)names.elementAt(i);
      result[i] = n.getName();
    }

    return result;
  }

  /**
   * Returns the component at the specified index.
   *
   * @param      index   an index into this list.
   * @return     the component at the specified index.
   */
  public final synchronized Object elementAt(int index) {
    return names.elementAt(index);
  }

  public Object clone() {
    NameableList newList = new NameableList(this.clazz);
    newList.names = (Vector)this.names.clone();

    return newList;
  }

  /**
   * Draft printing content of a list to console.
   *
   */
  public synchronized void printList() {
    for(int i=0; i < names.size(); ++i)
      System.out.println(names.elementAt(i));
  }

  public static void main(String[] args) throws IllegalArgumentException {
    NameableList list = new NameableList(Customer.class);

    Participant p1  = new Customer("user1");
    Participant p2  = new Customer("user2");
    Participant p3  = new Customer("user3");

    Participant p4  = new Supervisor("user4");

    list.add(p1);
    list.add(p2);
    list.add(p3);

    System.out.println("List with u1-u3:");
    list.printList();

    list.remove(p2);
    System.out.println("p2 removed");

    System.out.println("List without p2:");
    list.printList();

    Participant participant = (Participant)list.getElement("user1");
    if(participant == null)
      System.out.println("Participant \"user1\" does not exist");
    else
      System.out.println("Participant \"user1\" exist");

    participant = (Participant)list.getElement("user100");
    if(participant == null)
      System.out.println("Participant \"user100\" does not exist");
    else
      System.out.println("Participant \"user100\" exist");

  }

}
