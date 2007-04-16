// BigDigger.java

package org.google.code.netapps.bigdigger;

import java.io.File;
import java.io.Serializable;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.net.MalformedURLException;
import java.net.URL;

import org.google.code.servant.net.infoworm.InfoWormClient;
import org.google.code.servant.net.infoworm.InfoWorm;

/**
 * The main project class. Preform main business logic for getting
 * the subtree of the remote site.
 *
 * @version 1.1 08/20/2001
 * @author Alexander Shvets
 */
public class BigDigger implements Serializable {
  public static String serFileName = "BigDigger.ser";
  public static String logFileName = "BigDigger.log";

  private static String installDir;

  private List recFiles   = new ArrayList();
  private List plainFiles = new ArrayList();

  private int refsCounter = 0;

  private InfoWormClient client = new InfoWormClient();

  private HtmlFileScanner scanner;

  private String proxyHost;
  private int proxyPort;

  private URL startUrl;
  private boolean hostOnly;
  private boolean subdirsOnly;

  /**
   * Creates new retriever
   *
   * @param startUrl the start URL
   * @param hostOnly  looks only on this site
   * @param subdirsOnly  looks only in subdirs
   */
  public BigDigger(URL startUrl, boolean hostOnly, boolean subdirsOnly) 
         throws Exception {
    this.startUrl    = startUrl;
    this.hostOnly    = hostOnly;
    this.subdirsOnly = subdirsOnly;

    proxyHost = System.getProperty("proxyHost");
    proxyPort = Integer.getInteger("proxyPort", 80).intValue();

    if(proxyHost != null && proxyHost.length() == 0) {
      proxyHost = null;
    }

    if(proxyHost != null) {
      System.out.println("Working through the proxy server: " +
                          proxyHost + ":" + proxyPort + ".");
    }
    else {
      System.out.println("Using direct connection with remote servers.");
    }

    installDir = System.getProperty("install.dir");

    if(installDir == null) {
      installDir = new File(".").getCanonicalPath();
    }

    scanner = new HtmlFileScanner(hostOnly, subdirsOnly, 
                                  installDir + File.separator + logFileName);

    recFiles.add(UrlUtil.toString(startUrl));
  }

  /**
   * Separate step of analyze algorithm. Program goes through the list
   * of discovered files and tries to analyze each of them. During
   * ths step new files will be discovered. They will be analyzed 
   * on the next step.
   *
   */
  public void analyze() {
    String urlName = null;

    if(plainFiles.size() > 0) {
      urlName = (String)plainFiles.get(0);
      plainFiles.remove(0);
    }
    else {
      urlName = (String)recFiles.get(0);
      recFiles.remove(0);
    }

    URL url = null;

    try {
      url = new URL(urlName);
    }
    catch(MalformedURLException e) {
      System.out.println("Incorrect url: " + urlName);
      return;
    }

    ++refsCounter;

    int size = recFiles.size() + plainFiles.size();
    System.out.print(refsCounter + "(" + size + ") " + urlName);

    String host = url.getHost();
    int port    = 80;
    if(proxyHost != null) {
      host = proxyHost;
      port = proxyPort;
    }

    client.setHost(host);
    client.setPort(port);

    File localDirs = new File(UrlUtil.getPath(url));

    if(!localDirs.exists()) {
      localDirs.mkdirs();
    }

    InfoWorm response = null;
    try {
      InfoWorm request = createRequest(url);

      response = (InfoWorm)client.pipe(request)[0];
    }
    catch(IOException e) {
      System.out.println("\nError during communication with remote server " +
                         "(" + e.toString() + ")");
      return;
    }

    byte[] body = response.getBody();

    if(body != null) {
      String fileName = UrlUtil.getHostAndFile(url);

      BufferedOutputStream os = null;
      try {
        os = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
        os.write(body, 0, body.length);
      }
      catch(IOException e) {
        System.out.println("\nError during saving file " + fileName + "to local disk " +
                           "(" + e.toString() + ")");
        return;
      }
      finally {
        try {
          if(os != null)
            os.close();
        }
        catch(IOException e) {}
      }
    }

    long length    = getContentLength(response);
    int bodyLength = body.length;

    if(length == 0 || length == body.length) {
      System.out.println(" - " + body.length + "b - Ok");
    }
    else {
      System.out.println(" - " + body.length + "(" + length + ")b - bad size!");

      List header = response.getHeader();

      for(int i=0; i < header.size(); i++) {
        String field = (String)header.get(i);
        System.out.println(field);
      }
    }

    body = null;

    if(getContentType(response).equalsIgnoreCase("text/html")) {
      scanner.scan(url, recFiles, plainFiles);
    }
  }

  /**
   * Creates the request for web server
   *
   * @param url the url
   * @return the request for web server
   */
  private InfoWorm createRequest(URL url) {
    InfoWorm request = new InfoWorm();

    request.setField("GET " + url.toString() + " HTTP/1.0");

    return request;
  }

  /**
   * Checks if there is no files for analyze
   *
   * @return true if there is no files for analyze; false otherwise
   */
  public boolean isFinal() {
    return recFiles.isEmpty() && plainFiles.isEmpty();
  }

  /**
   * Gets the content length for a given info-worm
   *
   * @param infoWorm the info-worm
   * @return the content length value
   */
  public long getContentLength(InfoWorm infoWorm) {
    String lStr = infoWorm.getFieldValue("Content-Length");

    if(lStr == null || lStr.length() == 0)
      return 0;

    return Long.parseLong(lStr);
  }

  /**
   * Gets the content type for a given info-worm
   *
   * @param infoWorm the info-worm
   * @return the content type
   */
  public String getContentType(InfoWorm infoWorm)  {
    return infoWorm.getFieldValue("Content-Type");
  }

  /**
   * Main algorithm
   */
  public static void main(String[] args) throws Exception {
    if(args.length == 0) {
      System.out.println();
      System.out.println("Please Use : java org.javalobby.netapps.bigdigger.BigDigger [URL] [hostOnly] [subdirsOnly]");
      System.out.println("  where URL is the starting web page;");
      System.out.println("        hostOnly - looks only on this site;");
      System.out.println("        subdirsOnly - looks only in subdirs.");
      System.out.println("Author     : Alexander G. Shvets, Plainsboro, NJ, USA");
      System.out.println("e-mail     : shvets_alexander@yahoo.com");
      return;
    }

    File serFile = new File(installDir + File.separator + serFileName);
    ObjectSaver saver = new ObjectSaver(serFile);

    BigDigger analyzer = null;

    URL startUrl        = null;
    boolean hostOnly    = true;
    boolean subdirsOnly = true;

    if(serFile.exists()) {
      analyzer = (BigDigger)saver.restore();
      serFile.delete();
    }
    else {
      startUrl = new URL(args[0]);

      if(startUrl.getFile().equals("/")) {
        startUrl = new URL(startUrl, "index.html");
      }

      if(args.length > 1) {
        hostOnly = Boolean.valueOf(args[1]).booleanValue();
      }

      if(args.length > 2) {
        subdirsOnly = Boolean.valueOf(args[2]).booleanValue();
      }

      String fileName = UrlUtil.getHostAndFile(startUrl);

      if(new File(fileName).exists()) {
        System.out.println("File " + fileName + " already exists.");
        return;
      }

      analyzer = new BigDigger(startUrl, hostOnly, subdirsOnly);
    }

    KeyboardReader keyboardReader = new KeyboardReader();

    keyboardReader.start();

    while(!analyzer.isFinal()) {
      String command = keyboardReader.getLine();
      if(command != null) {
        if(command.equalsIgnoreCase("q")) break;
        keyboardReader.nextLine();
      }

      analyzer.analyze();
    }

    keyboardReader.stop();

    if(!analyzer.isFinal()) {
      saver.save(analyzer);
    }
    else {
      System.exit(0);
    }
  }

}
