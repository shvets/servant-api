// Tag.java

package org.google.code.netapps.bigdigger;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class represents http tag. Could dismember full tag on 
 * tag name and attributes.
 *
 * @version 1.1 08/20/2001
 * @author Alexander Shvets
 */
public class Tag {
  public static String A          = "A";
  public static String IMG        = "IMG";
  public static String FRAME      = "FRAME";
  public static String BODY       = "BODY";
  public static String BGSOUND    = "BGSOUND";
  public static String APPLET     = "APPLET";
  public static String PARAM      = "PARAM";
  public static String SCRIPT     = "SCRIPT";

/**
 * The description of common used tag attributes.
 *
 * @version 1.1 08/20/2001
 * @author Alexander Shvets
 */
public interface Attribute {

  public static String HREF       = "HREF";
  public static String SRC        = "SRC";
  public static String BACKGROUND = "BACKGROUND";
  public static String CODE       = "CODE";
  public static String CODEBASE   = "CODEBASE";
  public static String ARCHIVE    = "ARCHIVE";
  public static String OBJECT     = "OBJECT";
  public static String NAME       = "NAME";
  public static String VALUE      = "VALUE";

}


  /** The tag name */
  private String name = new String();

  /** The tag attributes */
  private Map attributes = new HashMap();

  /**
   * Creates new scanner
   */
  public Tag(String tag) {
    StringTokenizer st = new StringTokenizer(tag, "\n\r\t\'\"= ");

    if(st.hasMoreTokens()) {
      name = st.nextToken();

      while(st.hasMoreTokens()) {
        String attribute = st.nextToken().toUpperCase();

        if(st.hasMoreTokens()) {
          attributes.put(attribute, st.nextToken());
        }
      }
    }
  }

  /**
   * Gets the tag name
   *
   * @return the tag name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the tag attributes
   *
   * @return the tag attributes
   */
  public Map getAttributes() {
    return attributes;
  }

  /**
   * Gets the attribute value
   *
   * @param attribute  the attribute name
   * @return the attribute value
   */
  public String getValue(String attribute) {
    return (String)attributes.get(attribute.toUpperCase());
  }

  public static void main(String[] args) throws IOException {
    String tagString = "a href=\"programs.html\"";

    Tag tag = new Tag(tagString);

    System.out.println("tag: " + tagString);
    System.out.println("tag name: " + tag.getName());

    Map attributes = tag.getAttributes();

    Iterator iterator = attributes.keySet().iterator();

    while(iterator.hasNext()) {
      String attribute = (String)iterator.next();

      System.out.println("attribute: " + attribute);
      System.out.println("value    : " + attributes.get(attribute));
    }
  }

}
