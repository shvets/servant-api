// HtmlFileScanner.java

package org.google.code.netapps.bigdigger;

import org.google.code.servant.util.LogFile;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.URL;
import java.net.URLConnection;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.net.FileNameMap;


/**
 * The main project class. Preform main business logic for getting
 * the subtree of the remote site.
 *
 * @version 1.1 08/20/2001
 * @author Alexander Shvets
 */
public class HtmlFileScanner implements Serializable {
  private String[] extensions = new String[] {
    "au", "gif", "jpg", "jpeg", "bmp", "class", "jar", "zip", "cab", "js",
    "html", "htm", "asp", "jsp", "pdf"
  };

  private List errors = new ArrayList();

  private LogFile logFile;

  private URL url;
  private List recFiles;
  private List plainFiles;

  private boolean hostOnly;
  private boolean subdirsOnly;

  /**
   * Creates new file scanner
   *
   * @param hostOnly  looks only on this site
   * @param subdirsOnly  looks only in subdirs
   * @param logName the log file name
   */
  public HtmlFileScanner(boolean hostOnly, boolean subdirsOnly, String logName) {
    this.hostOnly    = hostOnly;
    this.subdirsOnly = subdirsOnly;

    try {
      logFile = new LogFile(logName, true);
    }
    catch(IOException e) {
      System.out.println("Cannot create file " + logName + ".");
    }
  }

  /**
   * Performs scanning of the file specified by URL
   *
   * @param url the url
   * @param recFiles the list of files with the HTML tags
   * @param plainFiles the list of regular files without the HTML tags
   */
  public void scan(URL url, List recFiles, List plainFiles) {
    this.url        = url;
    this.recFiles   = recFiles;
    this.plainFiles = plainFiles;

    errors.clear();
    errors.add(url.toExternalForm());

    String localFileName = UrlUtil.getHostAndFile(url);

    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(
                                      new FileInputStream(localFileName)));
    }
    catch(FileNotFoundException e) {
      System.out.println(e + ": " + localFileName);
      return;
    }

    try {
      int ch;
      while((ch = reader.read()) != -1) {
        if(ch == '<') {
          StringBuffer buffer = new StringBuffer();
          while(((ch = reader.read()) != -1) && (ch != '>')) {
            buffer.append((char)ch);
          }

          analyzeTag(buffer.toString());
        }
      }
    }
    catch(IOException e) {
      errors.add(e.toString());
    }

    if(errors.size() > 1) {
      logFile.logMessage((String)errors.get(0));
      for(int i=1; i < errors.size(); i++) {
        logFile.logMessage("   " + (String)errors.get(i));
      }
    }
  }

  /**
   * Analyzes separate tag
   *
   * @param text the text representation of the tag 
   */
  private void analyzeTag(String text) {
    Tag tag = new Tag(text);

    String tagName = tag.getName();

    if(tagName.equalsIgnoreCase(Tag.A)) {
      String hrefValue = tag.getValue(Tag.Attribute.HREF);
      if(isSupportedExtension(hrefValue)) {
        tryAddRef(hrefValue);
      }
    }
    else if(tagName.equalsIgnoreCase(Tag.BODY)) {
      String bgrValue = tag.getValue(Tag.Attribute.BACKGROUND);
      if(isSupportedExtension(bgrValue)) {
        tryAddRef(bgrValue);
      }
    }
    else if(tagName.equalsIgnoreCase(Tag.IMG)     ||
            tagName.equalsIgnoreCase(Tag.FRAME)   ||
            tagName.equalsIgnoreCase(Tag.BGSOUND) ||
            tagName.equalsIgnoreCase(Tag.SCRIPT)) {
      String srcValue = tag.getValue(Tag.Attribute.SRC);

      if(isSupportedExtension(srcValue)) {
        tryAddRef(srcValue);
      }
    }
    else if(tagName.equalsIgnoreCase(Tag.APPLET)) {
      String codeBaseValue = tag.getValue(Tag.Attribute.CODEBASE);

      if(codeBaseValue != null) {
        if(!codeBaseValue.endsWith("/"))
          codeBaseValue = codeBaseValue + '/';
      }
      else {
        codeBaseValue = "";
      }

      String archiveValue  = tag.getValue(Tag.Attribute.ARCHIVE);

      if(archiveValue != null) {
        String[] archives = split(archiveValue);
        for(int i=0; i < archives.length; i++) {
          tryAddRef(codeBaseValue + archives[i], "jar");
          tryAddRef(codeBaseValue + archives[i], "zip");
        }
      }
      else {
        String codeValue = tag.getValue(Tag.Attribute.CODE);
        tryAddRef(codeBaseValue + codeValue, "class");
      }

      String serValue = tag.getValue(Tag.Attribute.OBJECT);
      if(serValue != null)
        tryAddRef(codeBaseValue + serValue, "ser");
    }
    else if(tagName.equalsIgnoreCase(Tag.PARAM)) {
      String nameValue = tag.getValue(Tag.Attribute.NAME);
      if(nameValue != null) {
        String valueValue = tag.getValue(Tag.Attribute.VALUE);
        if(valueValue != null) {
          if(nameValue.equalsIgnoreCase(Tag.Attribute.ARCHIVE)) {
            tryAddRef(valueValue, "jar");
            tryAddRef(valueValue, "zip");
          }
          else if(nameValue.equalsIgnoreCase(Tag.Attribute.CODE)) {
            tryAddRef(valueValue, "class");
          }
          else if(valueValue.indexOf(',') != -1) {
            String[] names = split(valueValue);

            for(int i=0; i < names.length; i++) {
                if(isSupportedExtension(names[i])) {
                tryAddRef(names[i]);
              }
            }
          }
          else {
            if(isSupportedExtension(valueValue)) {
              tryAddRef(valueValue);
            }
          }
        }
      }
    }
  }

  /**
   * Splits the list of items, separated by comma or whitespace
   *
   * @param text  the list in form of string
   * @return separated elements in form of the array of strings 
   */
  private String[] split(String text) {
    List list = new ArrayList();

    StringTokenizer st = new StringTokenizer(text, "\n\r\t, ");
    while(st.hasMoreTokens()) {
      list.add(st.nextToken());
    }

    String[] al = new String[list.size()];
    list.toArray(al);

    return al;
  }

  /**
   * Checks if the extension is supported
   *
   * @param fileName  the file name
   * @return true, if the extension is supported; false otherwise
   */
  private boolean isSupportedExtension(String fileName) {
    if(fileName == null)
      return false;

    int index  = fileName.lastIndexOf(".");

    String ext = fileName.substring(index+1);

    if(ext != null && ext.length() > 0) {
      for(int i=0; i < extensions.length; i++) {
        if(extensions[i].equalsIgnoreCase(ext)) {
          return true;
        }
      }
    }

    FileNameMap map = URLConnection.getFileNameMap();
    String mimeType = map.getContentTypeFor(fileName);

    return (mimeType != null);
  }

  /**
   * Tries to add the reference
   *
   * @param ref the reference
   * @param ext the extension
   */
  private void tryAddRef(String ref, String ext) {
    if(ref == null)
      return;

    if(isSupportedExtension(ref)) {
      tryAddRef(ref);
    }
    else {
      tryAddRef(ref + "." + ext);
    }
  }

  /**
   * Tries to add the reference
   *
   * @param ref the reference
   */
  private void tryAddRef(String ref) {
    if(ref == null || ref.length() == 0)
      return;

    ref = ref.replace('\\', '/');

    URL currUrl = null;
    try {
      currUrl = new URL(url, ref);
    }
    catch(MalformedURLException e) {
      if(ref.startsWith("news:") || ref.startsWith("telnet:") ||
         ref.startsWith("javascript:")) {
        // do nothing
      }
      else {
        errors.add(ref + " - illegal reference.");
      }
      return;
    }

    if(new File(UrlUtil.getHostAndFile(currUrl)).exists())
      return;

    if(!hostOnly) {
      addRef(currUrl);
    }
    else {
      String host1 = currUrl.getHost();
      String host2 = url.getHost();

      if(!host1.equals(host2)) {
        String ia1 = null;
        String ia2 = null;

        try {
          ia1 = InetAddress.getByName(host1).getHostAddress();
        }
        catch(UnknownHostException e) {
          errors.add(ref + " - unknown host.");
          return;
        }

        try {
          ia2 = InetAddress.getByName(host2).getHostAddress();
        }
        catch(UnknownHostException e) {
          errors.add(url.toExternalForm() + " - unknown host.");
          return;
        }

        if(!ia1.equals(ia2)) {
          return;
        }
      }

      // in this point we have 2 URLs on the same site

      if(subdirsOnly) {
        String currDir = UrlUtil.getPath(currUrl);
        String dir     = UrlUtil.getPath(url);

        int curr_len   = (currDir == null) ? 0 : currDir.length();
        int len        = (dir == null)     ? 0 : dir.length();

        if(len < curr_len)
          currDir = dir.substring(0, len);

        if((dir == null) || dir.equalsIgnoreCase(currDir)) {
          addRef(currUrl);
        }
      }
    }
  }

  /**
   * Adds the url to the list
   *
   * @param url the url
   */
  private void addRef(URL url) {
    String urlName = UrlUtil.toString(url);

    if(urlName.endsWith(".htm") || urlName.endsWith(".html")) {
      if(!recFiles.contains(urlName))
        recFiles.add(urlName);
    }
    else {
      if(!plainFiles.contains(urlName))
        plainFiles.add(urlName);
    }
  }

  public static void main(String[] args) throws IOException {
    HtmlFileScanner scanner = new HtmlFileScanner(true, true, "bigDigger.log");

    URL startUrl      = new URL(args[0]);
    List recFiles   = new ArrayList();
    List plainFiles = new ArrayList();

    System.out.println("    " + startUrl);

    scanner.scan(startUrl, recFiles, plainFiles);

    Iterator iterator = recFiles.iterator();

    int i=0;
    while(iterator.hasNext()) {
      System.out.println((++i) + " " + iterator.next());
    }
  }

}

