/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.integration.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uk.ac.bbsrc.tgac.miso.integration.util
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 04/11/11
 * @since 0.1.3
 */
public class IntegrationUtils {
  protected static final Logger log = LoggerFactory.getLogger(IntegrationUtils.class);
  /**
   * Sets up the socket connection to a given host
   * 
   * @param host
   *          of type String
   * @param port
   *          of type int
   * @return Socket
   * @throws IntegrationException
   *           when the socket couldn't be created
   */
  public static Socket prepareSocket(String host, int port) throws IntegrationException {
    try {
      return new Socket(host, port);
    } catch (IOException e) {
      log.error("prepare socket", e);
      throw new IntegrationException("Cannot connect to " + host + ":" + port + ". Cause: " + e.getMessage());
    }
  }

  /**
   * Sends a String message to a given host socket
   * 
   * @param socket
   *          of type Socket
   * @param query
   *          of type String
   * @return String
   * @throws IntegrationException
   *           when the socket couldn't be created
   */
  public static String sendMessage(Socket socket, String query) throws IntegrationException {
    BufferedWriter wr = null;
    BufferedReader rd = null;
    try {
      wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));

      // Send data
      wr.write(query + "\r\n");
      wr.flush();

      // Get response
      rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = rd.readLine()) != null) {
        sb.append(line);
      }
      wr.close();
      rd.close();

      String dirty = sb.toString();
      StringBuilder response = new StringBuilder();
      int codePoint;
      int i = 0;
      while (i < dirty.length()) {
        codePoint = dirty.codePointAt(i);
        if ((codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
            || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
          response.append(Character.toChars(codePoint));
        }
        i += Character.charCount(codePoint);
      }

      return response.toString().replace("\\\n", "").replace("\\\t", "");
    } catch (UnknownHostException e) {
      log.error("Cannot resolve host: " + socket.getInetAddress(), e);
      throw new IntegrationException(e.getMessage());
    } catch (IOException e) {
      log.error("Couldn't get I/O for the connection to: " + socket.getInetAddress(), e);
      throw new IntegrationException(e.getMessage());
    } finally {
      try {
        if (wr != null) {
          wr.close();
        }
        if (rd != null) {
          rd.close();
        }
      } catch (Throwable t) {
        log.error("close socket", t);
      }
    }
  }

  public static byte[] compress(byte[] content) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Base64OutputStream b64os = new Base64OutputStream(baos);
    GZIPOutputStream gzip = new GZIPOutputStream(b64os);
    gzip.write(content);
    gzip.close();
    return baos.toByteArray();
  }

  public static byte[] decompress(byte[] contentBytes) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    GZIPInputStream bis = new GZIPInputStream(new Base64InputStream(new ByteArrayInputStream(contentBytes)));
    byte[] buffer = new byte[1024 * 4];
    int n = 0;
    while (-1 != (n = bis.read(buffer))) {
      out.write(buffer, 0, n);
    }
    return out.toByteArray();
  }
}
