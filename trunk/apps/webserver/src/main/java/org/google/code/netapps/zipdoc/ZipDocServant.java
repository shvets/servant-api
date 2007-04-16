// ZipDocServant.java

package org.google.code.netapps.zipdoc;

import org.google.code.netapps.web.WebServant;

import java.util.List;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * This class handles processing details, specific for zip doc server
 *
 * @version 1.0 08/20/2001
 * @author Alexander Shvets
 */
public class ZipDocServant extends WebServant {
  /** The zip doc server */
  private ZipDocServer server;

  /**
   * Creates new zip doc  servant
   *
   * @param server the zip doc server
   */
  public ZipDocServant(ZipDocServer server) {
    super(server);

    this.server = server;
  }

  /**
   * Performs GET request
   *
   * @param header  the header part of info-worm
   * @param fileName the name of the file
   * @exception  IOException  if an I/O error occurs.
   * @return the response as bytes array
   */
  protected byte[] doGet(List header, String fileName) throws IOException {
    int index = fileName.indexOf(".zip");

    if(index == -1) {
      return super.doGet(header, fileName);
    }

    String zipName = fileName.substring(0, index+4);

    ZipFile zipFile = new ZipFile(server.getRootDirectory() + zipName);

    String entryName = null;

    if(zipName.length() == fileName.length()) {
      return super.doGet(header, fileName);
    }
    else {
      entryName = fileName.substring(index+5);
    }

    if(entryName.length() == 0) {
      entryName = server.getIndexName();
    }

    System.out.println("entryName " + entryName);

    ZipEntry zipEntry = zipFile.getEntry(entryName);

    if(zipEntry == null) {
      return super.doGet(header, fileName);
    }

    FileNameMap map = URLConnection.getFileNameMap();
    String mimeType = map.getContentTypeFor(entryName);

    prepareHeader(header, "200", mimeType);

    return prepareZipEntry(zipFile, zipEntry);
  }

  /**
   * Prepares the zip entry in form of bytes array
   *
   * @param zipFile  the zip file
   * @param zipEntry the zip entry
   * @exception  IOException  if an I/O error occurs.
   * @return the zip entry in form of bytes array
   */
  private byte[] prepareZipEntry(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
    InputStream is = zipFile.getInputStream(zipEntry);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    while(true) {
      int ch = is.read();

      if(ch == -1) {
        break;
      }

      baos.write(ch);
    }

    is.close();
    baos.close();

    zipFile.close();

    return baos.toByteArray();
  }

}
