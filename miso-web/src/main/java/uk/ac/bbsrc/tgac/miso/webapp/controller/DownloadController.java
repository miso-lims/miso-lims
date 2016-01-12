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

package uk.ac.bbsrc.tgac.miso.webapp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.controller
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Controller
@RequestMapping("/download")
public class DownloadController {
  protected static final Logger log = LoggerFactory.getLogger(DownloadController.class);

  @Autowired
  private SecurityManager securityManager;

  @Autowired
  private RequestManager requestManager;

  @Autowired
  private FilesManager filesManager;

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }

  @RequestMapping(value = "/project/{id}/{hashcode}", method = RequestMethod.GET)
  protected void downloadProjectFile(@PathVariable Long id, @PathVariable Integer hashcode, HttpServletResponse response) throws Exception {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Project project = requestManager.getProjectById(id);
    if (project.userCanRead(user)) {
      lookupAndRetrieveFile(Project.class, id.toString(), hashcode, response);
    } else {
      throw new SecurityException("Access denied");
    }
  }

  @RequestMapping(value = "/plate/forms/{hashcode}", method = RequestMethod.GET)
  protected void downloadPlateExportFile(@PathVariable Integer hashcode, HttpServletResponse response) throws Exception {
    lookupAndRetrieveFile(Plate.class, "forms", hashcode, response);
  }

  @RequestMapping(value = "/plate/csv/{hashcode}", method = RequestMethod.GET)
  protected void downloadPlateCSVExportFile(@PathVariable Integer hashcode, HttpServletResponse response) throws Exception {
    lookupAndRetrieveFile(Plate.class, "csv", hashcode, response);
  }

  @RequestMapping(value = "/sample/forms/{hashcode}", method = RequestMethod.GET)
  protected void downloadSampleExportFile(@PathVariable Integer hashcode, HttpServletResponse response) throws Exception {
    lookupAndRetrieveFile(Sample.class, "forms", hashcode, response);
  }

  @RequestMapping(value = "/library/forms/{hashcode}", method = RequestMethod.GET)
  protected void downloadLibraryExportFile(@PathVariable Integer hashcode, HttpServletResponse response) throws Exception {
    lookupAndRetrieveFile(Library.class, "forms", hashcode, response);
  }

  @RequestMapping(value = "/submission/{id}/{hashcode}", method = RequestMethod.GET)
  protected void downloadSubmissionFile(@PathVariable Long id, @PathVariable Integer hashcode, HttpServletResponse response)
      throws Exception {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Submission submission = requestManager.getSubmissionById(id);
    if (submission.userCanRead(user)) {
      lookupAndRetrieveFile(Submission.class, "SUB" + id, hashcode, response);
    } else {
      throw new SecurityException("Access denied");
    }
  }

  @RequestMapping(value = "/libraryqc/{id}/{hashcode}", method = RequestMethod.GET)
  protected void downloadLibraryQcFile(@PathVariable Long id, @PathVariable Integer hashcode, HttpServletResponse response)
      throws Exception {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    Library library = requestManager.getLibraryById(id);
    if (library.userCanRead(user)) {
      lookupAndRetrieveFile(LibraryQC.class, id.toString(), hashcode, response);
    } else {
      throw new SecurityException("Access denied");
    }
  }

  @RequestMapping(value = "/sampleqc/{id}/{hashcode}", method = RequestMethod.GET)
  protected void downloadSampleQcFile(@PathVariable Long id, @PathVariable Integer hashcode, HttpServletResponse response)
      throws Exception {
    User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    SampleQC qc = requestManager.getSampleQCById(id);
    if (qc.userCanRead(user)) {
      lookupAndRetrieveFile(SampleQC.class, id.toString(), hashcode, response);
    } else {
      throw new SecurityException("Access denied");
    }
  }

  @RequestMapping(value = "/box/forms/{hashcode}", method = RequestMethod.GET)
  protected void downloadBoxContentsFile(@PathVariable Integer hashcode, HttpServletResponse response) throws Exception {
    lookupAndRetrieveFile(Box.class, "forms", hashcode, response);
  }

  private void lookupAndRetrieveFile(Class cl, String id, Integer hashcode, HttpServletResponse response) throws IOException {
    // lookup
    String filename = null;
    for (String s : filesManager.getFileNames(cl, id)) {
      if (s.hashCode() == hashcode) {
        filename = s;
      }
    }

    response.setHeader("Content-Disposition", "attachment; filename=" + filename);
    OutputStream responseStream = response.getOutputStream();

    // retrieval
    if (filename != null) {
      File file = filesManager.getFile(cl, id, filename);
      FileInputStream fis = new FileInputStream(file);
      int read = 0;
      byte[] bytes = new byte[1024];
      while ((read = fis.read(bytes)) != -1) {
        responseStream.write(bytes, 0, read);
      }
      responseStream.flush();
      responseStream.close();
    } else {
      throw new IOException("Cannot open file. Please check that it exists and is readable.");
    }
  }
}
