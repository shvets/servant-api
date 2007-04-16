// ObjectSaver.java

package org.google.code.netapps.bigdigger;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.File;

/**
 * Performs serialization/deserialization of the object.
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class ObjectSaver {

  /** The flie that will hold serialized object */
  private File serFile;

  /**
   * Creates new saver object with the specified file
   *
   * @param serFile  the file that will contain serialized object 
   */
  public ObjectSaver(File serFile) {
    this.serFile = serFile;
  }

  /**
   * Saves the object into file
   *
   * @param object the object to be serialized
   * @exception  IOException  if an I/O error occurs.
   */
  public void save(Object object) throws IOException  {
    FileOutputStream fos = new FileOutputStream(serFile);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(object);
    oos.flush();
    oos.close();
  }

  /**
   * Restores the object from file
   *
   * @exception  IOException  if an I/O error occurs.
   * @exception  ClassNotFoundException  if come class could not be found
   */
  public Object restore() throws IOException, ClassNotFoundException {
    FileInputStream fis = new FileInputStream(serFile);
    ObjectInputStream ois = new ObjectInputStream(fis);
    Object object = ois.readObject();
    ois.close();

    return object;
  }


  public static void main(String[] args) throws Exception {
    Integer i = new Integer(5);
    Integer j = new Integer(0);

    System.out.println("i = "+i);

    String serFileName = "Test.ser";
    File serFile = new File(serFileName);
    ObjectSaver saver = new ObjectSaver(serFile);

    saver.save(i);
    System.out.println("Saved...");

    j = (Integer)saver.restore();
    System.out.println("Restored...");

    System.out.println("i = "+j);

    System.out.println("Modified...");
    i = new Integer(100);
    System.out.println("i = "+i);

    saver.save(i);
    System.out.println("Saved...");

    j = (Integer)saver.restore();
    System.out.println("Restored...");

    System.out.println("i = "+j);
  }

}
