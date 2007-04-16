// KeyboardReader.java

package org.google.code.netapps.bigdigger;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class is trying to read from keyboard in form of 
 * separate process
 *
 * @version 1.1 08/20/2001
 * @author Alexander Shvets
 */
public class KeyboardReader implements Runnable {
  private transient Thread thread;

  private String line;
  private BufferedReader reader;
  private boolean done;

  /**
   * Creates new keyboard reader
   */
  public KeyboardReader() {
    reader = new BufferedReader(new InputStreamReader(System.in));
  }

  /**
   * Starts the process of reading
   */
  public void start() {
    done = false;

    if(thread == null) {
      thread = new Thread(this);
    }

    thread.start();
  }

  /**
   * Stops the process of reading
   */
  public void stop() {
    done = true;

    thread.interrupt();

    try {
      thread.join();
    }
    catch(InterruptedException e) {
      System.out.println(e);
    }

    thread = null;
  }

  /**
   * Gets the next line from the stream
   *
   * @return  the next line from the stream
   */
  public String getLine() {
    return line;
  }

  /**
   * Clears the next line
   *
   */
  public synchronized void nextLine() {
    line = null;
  }

  /**
   * Main thread life
   */
  public void run() {
    while(!done) {
      try {
        if(line == null) {
          synchronized(this) {
            line = reader.readLine();
          }
        }
      }
      catch(IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args)
                     throws IOException, ClassNotFoundException {
    String serFileName = "Test.ser";
    File serFile = new File(serFileName);
    ObjectSaver saver = new ObjectSaver(serFile);

    int cnt = 0;

    if(serFile.exists()) {
      cnt = ((Integer)saver.restore()).intValue();
      serFile.delete();
    }

    KeyboardReader keyboardReader = new KeyboardReader();

    keyboardReader.start();

    while(true) {
      if(cnt > 10000) break;

      String line = keyboardReader.getLine();

      if(line != null) {
        if(line.equalsIgnoreCase("q")) break;

        keyboardReader.nextLine();
      }

      ++cnt;
      System.out.println(cnt);
    }

    keyboardReader.stop();

    if(cnt < 5000) {
      saver.save(new Integer(cnt));
    }
  }

}

