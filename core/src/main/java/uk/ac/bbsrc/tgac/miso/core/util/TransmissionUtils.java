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

import static org.apache.commons.net.io.Util.copyStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * uk.ac.bbsrc.tgac.miso.core.util
 * <p/>
 * A simple class to provide methods to transmit files to and from services.
 * 
 * @author Rob Davey
 * @date 29-Jun-2011
 * @since 0.0.3
 */
public class TransmissionUtils {
  protected static final Logger log = LoggerFactory.getLogger(TransmissionUtils.class);

  public static FTPClient ftpConnect(String host, String username, String password) throws IOException {
    FTPClient ftp = new FTPClient();
    try {

      ftp.connect(host);
      log.debug("Trying " + host);
      log.debug(ftp.getReplyString());
      int reply = ftp.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        ftp.disconnect();
        throw new IOException("FTP server refused connection: " + reply);
      } else {
        log.info("Connected");
      }

      ftp.login(username, password);
      ftp.setFileType(FTP.BINARY_FILE_TYPE);
      ftp.enterLocalPassiveMode();
    } catch (NoRouteToHostException e) {
      throw new IOException("Couldn't connect to printer: " + e.getMessage(), e);
    } catch (UnknownHostException e) {
      throw new IOException("Couldn't connect to printer: " + e.getMessage(), e);
    }
    return ftp;
  }

  public static FTPClient ftpConnect(String host, int port, String username, String password) throws IOException {
    FTPClient ftp = new FTPClient();
    try {
      ftp.connect(host, port);
      log.debug("Trying " + host + ":" + port);
      log.debug(ftp.getReplyString());
      int reply = ftp.getReplyCode();
      if (!FTPReply.isPositiveCompletion(reply)) {
        ftp.disconnect();
        throw new IOException("FTP server refused connection: " + reply);
      } else {
        log.debug("Connected");
      }

      ftp.login(username, password);
      ftp.setFileType(FTP.BINARY_FILE_TYPE);
      ftp.enterLocalPassiveMode();
    } catch (NoRouteToHostException e) {
      throw new IOException("Couldn't connect to printer: " + e.getMessage(), e);
    } catch (UnknownHostException e) {
      throw new IOException("Couldn't connect to printer: " + e.getMessage(), e);
    }
    return ftp;
  }

  public static boolean ftpPut(FTPClient ftp, List<File> files, boolean autoLogout) throws IOException {
    return ftpPut(ftp, null, files, autoLogout, false);
  }

  public static boolean ftpPut(FTPClient ftp, String path, List<File> files, boolean autoLogout, boolean autoMkdir) throws IOException {
    boolean error = false;
    FileInputStream fis = null;

    try {
      if (ftp == null || !ftp.isConnected()) {
        error = true;
        throw new IOException("FTP client isn't connected. Please supply a client that has connected to the host.");
      }

      if (path != null) {
        if (autoMkdir) {
          if (!ftp.makeDirectory(path)) {
            error = true;
            throw new IOException("Cannot create desired path on the server.");
          }
        }

        if (!ftp.changeWorkingDirectory(path)) {
          error = true;
          throw new IOException("Desired path does not exist on the server");
        }
      }

      log.info("All OK - transmitting " + files.size() + " file(s)");

      for (File f : files) {
        fis = new FileInputStream(f);
        if (!ftp.storeFile(f.getName(), fis)) {
          error = true;
          log.error("Error storing file: " + f.getName());
        }

        boolean success = FTPReply.isPositiveCompletion(ftp.getReplyCode());
        if (!success) {
          error = true;
          log.error("Error storing file: " + f.getName() + " (" + success + ")");
        }
      }

      if (autoLogout) {
        ftp.logout();
      }
    } catch (IOException e) {
      error = true;
      log.error("ftp", e);
    } finally {
      try {
        if (fis != null) {
          fis.close();
        }

        if (autoLogout) {
          if (ftp != null && ftp.isConnected()) {
            ftp.disconnect();
          }
        }
      } catch (IOException ioe) {
        log.error("ftp", ioe);
      }
    }

    // return inverse error boolean, just to make downstream conditionals easier
    return !error;
  }

  public static boolean ftpPutListen(FTPClient ftp, String path, File file, boolean autoLogout, boolean autoMkdir,
      CopyStreamListener listener) throws IOException {
    boolean error = false;
    FileInputStream fis = null;

    log.info("ftpPutListen has been called for file:" + file.getName());
    try {
      if (ftp == null || !ftp.isConnected()) {
        error = true;
        throw new IOException("FTP client isn't connected. Please supply a client that has connected to the host.");
      }

      if (path != null) {
        if (autoMkdir) {
          if (!ftp.makeDirectory(path)) {
            error = true;
            throw new IOException("Cannot create desired path on the server.");
          }
        }
        log.info("Working dir =" + ftp.printWorkingDirectory());
        if (!ftp.changeWorkingDirectory(path)) {
          error = true;
          throw new IOException("Desired path does not exist on the server");
        }
      }

      fis = new FileInputStream(file);

      OutputStream ops = new BufferedOutputStream(ftp.storeFileStream(file.getName()), ftp.getBufferSize());

      log.info("TransmissionUtils putListen: FTP server responded: " + ftp.getReplyString());

      copyStream(fis, ops, ftp.getBufferSize(), file.length(), listener);

      ops.close();
      fis.close();
      log.info("TransmissionUtils putListen: FTP server responded: " + ftp.getReplyString());

      if (autoLogout) {
        ftp.logout();
      }
    } catch (IOException e) {
      error = true;
      log.error("ftp put listen", e);
    } finally {
      try {
        log.info("TransmissionUtils putListen:finally: " + ftp.getReplyString());
        if (fis != null) {
          fis.close();
        }

        if (autoLogout) {
          if (ftp != null && ftp.isConnected()) {
            ftp.disconnect();
          }
        }
      } catch (IOException ioe) {
        log.error("ftp put listen close", ioe);
      }
    }

    // return inverse error boolean, just to make downstream conditionals easier
    log.info("result of transmissionutils.putListen:", !error);
    return !error;

  }
}
