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

package uk.ac.bbsrc.tgac.miso.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.exception.InterrogationException;

/**
 * Utility class that provides helpful method to interrogate sequencers using JSON queries sent to a socket running MISOs Perl Interrogation
 * Daemon
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class SequencerInterrogationUtils {
  protected static final Logger log = LoggerFactory.getLogger(SequencerInterrogationUtils.class);
  /**
   * Sets up the socket connection to a given SequencerReference
   * 
   * @param sr
   *          of type SequencerReference
   * @return Socket
   * @throws InterrogationException
   *           when the socket couldn't be created
   */
  public static Socket prepareSocket(SequencerReference sr) throws InterrogationException {
    // TODO - don't hard code the port. should be stored in with the sequencer reference, i.e. sr.getPort()!
    try {
      return new Socket(sr.getIpAddress(), 7899);
    } catch (IOException e) {
      log.error("prepare socket", e);
      throw new InterrogationException(e.getMessage());
    }
  }

  /**
   * Query the daemon running on a given socket with a given JSON query. Queries should be in the following format:
   * <p/>
   * TODO - query examples
   * 
   * @param socket
   *          of type Socket
   * @param jsonQuery
   *          of type String
   * @return String
   * @throws InterrogationException
   *           when the socket could not be interrogated
   */
  public static String querySocket(Socket socket, String jsonQuery) throws InterrogationException {
    PrintWriter out = null;
    BufferedReader in = null;

    try {
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out.println(jsonQuery);

      StringBuilder sb = new StringBuilder();
      String line = null;
      while ((line = in.readLine()) != null) {
        System.out.println(line);
        sb.append(line);
      }

      out.close();
      in.close();
      socket.close();
      String dirty = sb.toString();

      log.info(dirty);

      StringBuilder response = new StringBuilder(); // Used to hold the output.
      int codePoint; // Used to reference the current character.
      int i = 0;
      while (i < dirty.length()) {
        codePoint = dirty.codePointAt(i); // This is the unicode code of the character.
        if ((codePoint == 0x9) || // Consider testing larger ranges first to improve speed.
            (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
            || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
          response.append(Character.toChars(codePoint));
        }
        i += Character.charCount(codePoint); // Increment with the number of code units(java chars) needed to represent a Unicode char.
      }

      String clean = response.toString().replace("\\\n", "").replace("\\\t", "");

      log.info(clean);

      return clean;
    } catch (UnknownHostException e) {
      log.error("Cannot resolve host: " + socket.getInetAddress(), e);
      throw new InterrogationException(e.getMessage());
    } catch (IOException e) {
      log.error("Couldn't get I/O for the connection to: " + socket.getInetAddress(), e);
      throw new InterrogationException(e.getMessage());
    }
  }
}
