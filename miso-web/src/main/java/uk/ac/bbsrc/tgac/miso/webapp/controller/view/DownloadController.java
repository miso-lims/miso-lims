package uk.ac.bbsrc.tgac.miso.webapp.controller.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletResponse;
import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.qc.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.QcTarget;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.core.service.QualityControlService;
import uk.ac.bbsrc.tgac.miso.webapp.util.MisoWebUtils;

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
  private FilesManager filesManager;

  @Autowired
  private LibraryService libraryService;

  @Autowired
  private QualityControlService qcService;

  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  @RequestMapping(value = "/sample/forms/{hashcode}", method = RequestMethod.GET)
  protected void downloadSampleExportFile(@PathVariable Integer hashcode, HttpServletResponse response)
      throws Exception {
    lookupAndRetrieveFile(Sample.class, "forms", hashcode, response);
  }

  @RequestMapping(value = "/library/forms/{hashcode}", method = RequestMethod.GET)
  protected void downloadLibraryExportFile(@PathVariable Integer hashcode, HttpServletResponse response)
      throws Exception {
    lookupAndRetrieveFile(Library.class, "forms", hashcode, response);
  }

  @RequestMapping(value = "/submission/{id}/{hashcode}", method = RequestMethod.GET)
  protected void downloadSubmissionFile(@PathVariable Long id, @PathVariable Integer hashcode,
      HttpServletResponse response)
      throws Exception {
    lookupAndRetrieveFile(Submission.class, "SUB" + id, hashcode, response);
  }

  @RequestMapping(value = "/libraryqc/{id}/{hashcode}", method = RequestMethod.GET)
  protected void downloadLibraryQcFile(@PathVariable Long id, @PathVariable Integer hashcode,
      HttpServletResponse response)
      throws IOException {
    libraryService.get(id); // service invoked for permission check only
    lookupAndRetrieveFile(LibraryQC.class, id.toString(), hashcode, response);
  }

  @RequestMapping(value = "/sampleqc/{id}/{hashcode}", method = RequestMethod.GET)
  protected void downloadSampleQcFile(@PathVariable Long id, @PathVariable Integer hashcode,
      HttpServletResponse response)
      throws Exception {
    qcService.get(QcTarget.Sample, id); // service invoked for permission check only
    lookupAndRetrieveFile(SampleQC.class, id.toString(), hashcode, response);
  }

  @RequestMapping(value = "/box/forms/{hashcode}", method = RequestMethod.GET)
  protected void downloadBoxContentsFile(@PathVariable Integer hashcode, HttpServletResponse response)
      throws Exception {
    lookupAndRetrieveFile(Box.class, "forms", hashcode, response);
  }

  private void lookupAndRetrieveFile(Class<?> cl, String id, Integer hashcode, HttpServletResponse response)
      throws IOException {
    // lookup
    String filename = null;
    for (String s : filesManager.getFileNames(cl, id)) {
      if (s.hashCode() == hashcode) {
        filename = s;
      }
    }

    MisoWebUtils.addAttachmentContentDisposition(response, filename);
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
      fis.close();
    } else {
      throw new IOException("Cannot open file. Please check that it exists and is readable.");
    }
  }
}
