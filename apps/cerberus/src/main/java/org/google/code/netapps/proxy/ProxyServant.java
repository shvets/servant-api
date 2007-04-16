// ProxyServant.java

package org.google.code.netapps.proxy;

import org.google.code.servant.net.infoworm.InfoWormServant;
import org.google.code.servant.net.infoworm.InfoWorm;
import org.google.code.servant.net.infoworm.InfoWormInputStream;
import org.google.code.servant.net.infoworm.InfoWormOutputStream;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.List;

/**
 * This class handles processing details, specific for proxy server
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class ProxyServant extends InfoWormServant {
  /** The cache pool */
  private CachePool cachePool;

  /** The proxy server */
  private ProxyServer server;

  /**
   * Creates new proxy servant
   *
   * @param server the web server
   */
  public ProxyServant(ProxyServer server) {
    super(server);

    this.server = server;

    cachePool = server.getCachePool();
  }

  /**
   * Performs the "service" routine: for ech client's request the servant 
   * should prepare responses array
   *
   * @param requestObject  the request from the client
   * @return  the responses list
   * @exception  IOException  if an I/O error occurs.
   */
  public Object[] service(Object requestObject) throws IOException {    
    InfoWorm request = (InfoWorm)requestObject;

    List requestHeader = request.getHeader();

    String[] requestTitle = parse((String)requestHeader.get(0));
    String method = requestTitle[0];
    String urlStr = requestTitle[1];

    InfoWorm response = new InfoWorm();

    // 2. trying to read response from cache pool
    if(cachePool.isCacheEnabled()) {
      if(method.equals("GET") || method.equals("POST")) {
        CacheObject cache = cachePool.getCache(urlStr);
        if(cache != null) {
          response = cache.getInfoWorm();

          long cacheExpire = server.getCacheExpire() * 3600000;
          cache.setExpiration(new Date(new Date().getTime() + cacheExpire));

          return new InfoWorm[] { response };
        }
      }
    }

    // 3. preparing to communicate with remote server
    //    (directly or over other proxy)
    URL url     = new URL(urlStr);
    String host = url.getHost();
    int port    = url.getPort();

    if(port == -1) {
      port = 80;
    }

    if(server.usingProxy()) {
      host = server.getProxyHost();
      port = server.getProxyPort();
    }

    // 4. write request to a remote server
    Socket socket2 = new Socket(host, port);
    InfoWormInputStream in2   = new InfoWormInputStream(socket2.getInputStream());
    InfoWormOutputStream out2 = new InfoWormOutputStream(socket2.getOutputStream());

    socket2.setSoTimeout(server.getSoTimeout());

    out2.writeInfoWorm(request);

    // 5. read responce from a remote server
    response = in2.readInfoWorm();

    // 6. close connection with a remote server
    in2.close();
    out2.close();
    socket2.close();

    System.out.println("response: " + response);

    // 7. save downloaded file to the chache pool
    if(cachePool.isCacheEnabled()) {
      if(method.equals("GET") || method.equals("POST")) {
        List responseHeader  = response.getHeader();
        String[] responseTitle = parse((String)responseHeader.get(0));

        String body = responseTitle[1];

        if(body.startsWith("2")) {
          CacheObject cache = new CacheObject(urlStr, response);

          long cacheExpire = server.getCacheExpire() * 3600000;
          cache.setExpiration(new Date(new Date().getTime() + cacheExpire));

          cachePool.addCacheObject(cache);
        }
      }
    }

    // 8. some additional services of proxy server
    //    (save images to a special directory)
    if(server.isSaveImages()) {
      String contentType = response.getFieldValue("Content-Type");

      if(contentType != null && contentType.startsWith("image")) {
        String fullName = url.getFile();
        int index = fullName.lastIndexOf("/");
        if(index != -1) {
          String fileName = server.getSavedImagesDirectory() + fullName.substring(index);
          File f = new File(fileName);
          FileOutputStream fos = new FileOutputStream(f);
          fos.write(response.getBody());
          fos.close();
        }
      }
    }

    return new InfoWorm[] { response };
  }

  /**
   * Parses the first line of the request to extract elements like
   * method name, file name etc.
   *
   * @param s the string to be parsed
   * @return the array of strings
   */
  private String[] parse(String s) {
    StringTokenizer st = new StringTokenizer(s);

    String[] answer = new String[3];

    answer[0] = st.nextToken();
    answer[1] = st.nextToken();
    answer[2] = st.nextToken();

    return answer;
  }

}
