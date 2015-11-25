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

package uk.ac.bbsrc.tgac.miso.webapp.service.integration.illumina;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;

import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.service.integration.illumina
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 07-Mar-2011
 * @since 0.0.3
 */
public class IlluminaCortexStatsHandler {
  private Logger log = LoggerFactory.getLogger(getClass());

  @Autowired
  private MisoFilesManager misoFileManager;

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void parseStatsMessage(Message<Map<String, Map<String, byte[]>>> message) throws IOException {
    Map<String, Map<String, byte[]>> a = message.getPayload();
    for (String type : a.keySet()) {
      Map<String, byte[]> b = a.get(type);
      for (String filename : b.keySet()) {
        log.info("Processing stats for: " + filename);
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(b.get(filename)));
        File outFile = null;
        OutputStream out = null;
        try {
          outFile = new File(new File("/tmp/"), filename);
          try {
            out = new FileOutputStream(outFile);
            byte[] buf = new byte[16884];
            int len;
            while ((len = bis.read(buf)) > 0) {
              out.write(buf, 0, len);
            }
          } catch (IOException e) {
            log.error("Could not write temporary file: " + outFile.getAbsolutePath(), e);
          } finally {
            try {
              bis.close();
            } catch (IOException e) {
              // ignore
            }
          }
        } finally {
          if (out != null) {
            out.close();
          }
          if (outFile != null) {
            File newFile = misoFileManager.storeFile(Run.class, "stats", outFile);
            if (newFile != null && outFile.delete()) {
              File destination = new File(newFile.getParentFile(), newFile.getName().split("-")[0]);
              if (LimsUtils.checkDirectory(destination, true)) {
                if (LimsUtils.unzipFile(newFile, destination)) {
                  newFile.delete();
                }
              }
            }
          }
        }
      }
    }
  }
}
