// InfoWormInputStream.java

package org.google.code.servant.net.infoworm;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * This class can read InfoWorm objects and their parts.
 *
 * @version 1.0 03/20/2000
 * @author Alexander Shvets
 */
public class InfoWormInputStream extends FilterInputStream {

  /**
   * Creates new InfoWorm input stream.
   *
   * @param is  the input stream to be wrapped
   */
  public InfoWormInputStream(InputStream is) {
    super(is);
  }

  /**
   * Reads complete InfoWorm object.
   *
   * @return  InfoWorm object that have built from the stream
   * @exception  IOException  if an I/O error occurs.
   */
  public InfoWorm readInfoWorm() throws IOException {
    return new InfoWorm(readHeader(), readBody());
  }

  /**
   * Reads the header part.
   *
   * @return the header part
   * @exception  IOException  if an I/O error occurs.
   */
  public List readHeader() throws IOException {
    List header = new ArrayList();

    while (true) {
      String line = readLine(in);
      if(line == null) {
        throw new IOException("Error during reading header.");
      }

      line = line.trim();

      if(line.equals("")) {
        break;
      }

      header.add(line);
    }

    return header;
  }

  /**
   * Reads the body part. The size of the body is unknown.
   *
   * @return the body part
   * @exception  IOException  if an I/O error occurs.
   */
  public byte[] readBody() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    byte[] buf = new byte[2048];
    int n = 0;
    while(true) {
      n = in.read(buf);
      if(n == -1)
        break;

      baos.write(buf, 0, n);
      baos.flush();
    }

    baos.close();

    return baos.toByteArray();
  }

  /**
   * Reads the body part. The size of the body is known before reading.
   *
   * @size  the size of body
   * @return the body part
   * @exception  IOException  if an I/O error occurs.
   */
  public byte[] readBody(long size) throws IOException {
    byte[] buffer = new byte[(int)size];

    in.read(buffer);

    return buffer;
  }

  /** The char buffer for saving reading */
  private char lineBuffer[];

  /**
   * Reads new line form input stream.
   *
   * @param in  the input stream
   * @exception  IOException  if an I/O error occurs.
   */
  public String readLine(InputStream in) throws IOException {
    char buf[] = lineBuffer;

    if(buf == null) {
       buf = lineBuffer = new char[128];
    }

    int room = buf.length;
    int offset = 0;
    int c;

    loop: while (true) {
      c = in.read();

      switch(c) {
        case -1:
        case '\n':
          break loop;

        case '\r':
          int c2 = in.read();
          if(c2 != '\n') {
            if (!(in instanceof PushbackInputStream)) {
              in = new PushbackInputStream(in);
            }
            ((PushbackInputStream)in).unread(c2);
          }
          break loop;

        default:
          if(--room < 0) {
            buf = new char[offset + 128];
            room = buf.length - offset - 1;
            System.arraycopy(lineBuffer, 0, buf, 0, offset);
            lineBuffer = buf;
          }
          buf[offset++] = (char) c;
          break;
      }
    }

    if((c == -1) && (offset == 0)) {
      return null;
    }

    return String.copyValueOf(buf, 0, offset);
  }

}
