// InfoWormOutputStream.java

package org.google.code.servant.net.infoworm;

import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * This class can write InfoWorm obects and their parts.
 *
 * @version 1.0 03/20/2000
 * @author Alexander Shvets
 */
public class InfoWormOutputStream extends FilterOutputStream {

  /**
   * Creates new InfoWorm output stream.
   *
   * @param os  the output stream to be wrapped
   */
  public InfoWormOutputStream(OutputStream os) {
    super(os);
  }

  /**
   * Writes complete InfoWorm object.
   *
   * @param infoWorm InfoWorm object that will be streamed
   * @exception  IOException  if an I/O error occurs.
   */
  public void writeInfoWorm(InfoWorm infoWorm) throws IOException {
    writeHeader(infoWorm.getHeader());
    writeBody(infoWorm.getBody());
  }

  /**
   * Writes the header part.
   *
   * @param header the header part
   * @exception  IOException  if an I/O error occurs.
   */
  public void writeHeader(List header) throws IOException {
    if(header != null) {
      for(int i=0; i < header.size(); i++) {
        String field = (String)header.get(i);

        out.write((field + "\r\n").getBytes());
      }

      out.write("\r\n".getBytes());
      out.flush();
    }
  }

  /**
   * Writes the body part.
   *
   * @param body the body part
   * @exception  IOException  if an I/O error occurs.
   */
  public void writeBody(byte[] body) throws IOException {
    if(body != null) {
      out.write(body);
      out.flush();
    }
  }

}
