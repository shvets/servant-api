/*
 * @(#)Customer.java 1.0 06/24/99
 *
 */

package org.google.code.netapps.chat.primitive;

/**
 * Class for representing a customer.
 *
 * @version 1.0 06/24/99
 * @author Alexander Shvets
 */
public class Customer extends Participant {

  /**
   * Constructs a customer with the specified name.
   *
   * @param   name   the name of a customer
   */
  public Customer(String name) {
    super(name);
  }

  /**
   * Get type.
   *
   * @return  type
   */
  public String getType() {
    return ParticipantType.CUSTOMER;
  }

  public static void main(String[] args) {
    Customer c1 = new Customer("alex");
    Customer c2 = new Customer("nick");
    Customer c3 = new Customer("igor");

    System.out.println("Customer c1 : " + c1);
    System.out.println("Customer c2 : " + c2);
    System.out.println("Customer c3 : " + c3);

    System.out.println("Customer c2 equals c3 : " + c2.equals(c3));

    System.out.println("Customer c3 equals \"igor\" : " + c3.equals("igor"));

    Participant p1 = new Participant("igor");

    System.out.println("Customer c2 equals p1 : " + c2.equals(p1));
  }

}