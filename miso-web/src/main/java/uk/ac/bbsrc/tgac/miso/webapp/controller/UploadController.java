/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.IndexService;
import uk.ac.bbsrc.tgac.miso.core.service.naming.NamingScheme;
import uk.ac.bbsrc.tgac.miso.core.store.LibraryQcStore;
import uk.ac.bbsrc.tgac.miso.core.store.SampleQcStore;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.LibraryService;
import uk.ac.bbsrc.tgac.miso.service.SampleService;
import uk.ac.bbsrc.tgac.miso.spring.util.FormUtils;
import uk.ac.bbsrc.tgac.miso.webapp.service.forms.MisoFormsService;

@Controller
@RequestMapping("/upload")
public class UploadController {
  protected static final Logger log = LoggerFactory.getLogger(UploadController.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  public RequestManager requestManager;
  @Autowired
  public FilesManager filesManager;
  @Autowired
  public MisoFormsService misoFormsService;
  @Autowired
  private NamingScheme namingScheme;
  @Autowired
  private IndexService tagBarcodeService;
  @Autowired
  private LibraryService libraryService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private SampleQcStore sampleQcStore;
  @Autowired
  private LibraryQcStore libraryQcStore;

  public void setTagBarcodeService(IndexService tagBarcodeService) {
    this.tagBarcodeService = tagBarcodeService;
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }

  public void setMisoFormsService(MisoFormsService misoFormsService) {
    this.misoFormsService = misoFormsService;
  }

  public void setLibraryNamingScheme(NamingScheme namingScheme) {
    this.namingScheme = namingScheme;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public void setSampleQcStore(SampleQcStore sampleQcStore) {
    this.sampleQcStore = sampleQcStore;
  }

  public void setLibraryQcStore(LibraryQcStore libraryQcStore) {
    this.libraryQcStore = libraryQcStore;
  }

  public void uploadFile(Class<?> type, String qualifier, MultipartFile fileItem) throws IOException {
    File dir = new File(
        filesManager.getFileStorageDirectory() + File.separator + type.getSimpleName().toLowerCase() + File.separator + qualifier);
    if (LimsUtils.checkDirectory(dir, true)) {
      log.info("Attempting to store " + dir.toString() + File.separator + fileItem.getOriginalFilename());
      fileItem.transferTo(new File(dir + File.separator + fileItem.getOriginalFilename().replaceAll("\\s", "_")));
    } else {
      throw new IOException("Cannot upload file - check that the directory specified in miso.properties exists and is writable");
    }
  }

  public void uploadFile(Object type, String qualifier, MultipartFile fileItem) throws IOException {
    uploadFile(type.getClass(), qualifier, fileItem);
  }

  @RequestMapping(value = "/project", method = RequestMethod.POST)
  public void uploadProjectDocument(MultipartHttpServletRequest request) throws IOException {
    String projectId = request.getParameter("projectId");
    if (projectId == null) {
      throw new IOException("Cannot upload file - projectId parameter missing or null");
    } else if (requestManager.getProjectById(Long.valueOf(projectId)) == null) {
      throw new IOException("Cannot upload file - service record does not exist");
    }

    for (MultipartFile fileItem : getMultipartFiles(request)) {
      uploadFile(Project.class, projectId, fileItem);
    }
  }

  @RequestMapping(value = "/project/sample-delivery-form", method = RequestMethod.POST)
  public String uploadProjectSampleDeliveryForm(MultipartHttpServletRequest request) throws IOException {
    String projectId = request.getParameter("projectId");
    if (projectId == null) {
      throw new IOException("Cannot upload file - projectId parameter missing or null");
    } else if (requestManager.getProjectById(Long.valueOf(projectId)) == null) {
      throw new IOException("Cannot upload file - project does not exist");
    }

    boolean taxonCheck = (Boolean) request.getSession().getServletContext().getAttribute("taxonLookupEnabled");

    try {
      for (MultipartFile fileItem : getMultipartFiles(request)) {
        uploadFile(Project.class, projectId, fileItem);
        File f = filesManager.getFile(Project.class, projectId, fileItem.getOriginalFilename().replaceAll("\\s+", "_"));
        List<Sample> samples = FormUtils.importSampleDeliveryForm(f);
        log.info("Importing samples from form: " + samples.toString());
        misoFormsService.importSampleDeliveryFormSamples(samples, taxonCheck);
      }
      return "redirect:/miso/project/" + projectId;
    } catch (Exception e) {
      log.error("SAMPLE IMPORT FAIL:", e);
      throw new IOException(e);
    }
  }

  @RequestMapping(value = "/project/bulk-input-form", method = RequestMethod.POST)
  public void uploadProjectBulkInputForm(MultipartHttpServletRequest request) throws IOException {
    String projectId = request.getParameter("projectId");
    if (projectId == null) {
      throw new IOException("Cannot upload file - projectId parameter missing or null");
    } else if (requestManager.getProjectById(Long.valueOf(projectId)) == null) {
      throw new IOException("Cannot upload file - project does not exist");
    }

    try {
      for (MultipartFile fileItem : getMultipartFiles(request)) {
        uploadFile(Project.class, projectId, fileItem);
        File f = filesManager.getFile(Project.class, projectId, fileItem.getOriginalFilename().replaceAll("\\s+", "_"));
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Sample> samples = FormUtils.importSampleInputSpreadsheet(f, user, sampleService, libraryService, sampleQcStore,
            libraryQcStore,
            namingScheme, tagBarcodeService);

        ObjectMapper mapper = new ObjectMapper();

        JSONArray a = new JSONArray();
        for (Sample s : samples) {
          a.add(JSONObject.fromObject(mapper.writeValueAsString(s)));
        }
        JSONObject o = new JSONObject();
        o.put("bulksamples", a);
        log.info(o.toString());

        request.getSession(false).setAttribute("bulksamples", o);
      }
    } catch (Exception e) {
      log.error("SAMPLE IMPORT FAIL", e);
    }
  }

  @RequestMapping(value = "/importexport/samplesheet", method = RequestMethod.POST)
  public void uploadSampleSheet(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      JSONArray jsonArray = new JSONArray();
      for (MultipartFile fileItem : getMultipartFiles(request)) {
        uploadFile(Sample.class, "forms", fileItem);
        File f = filesManager.getFile(Sample.class, "forms", fileItem.getOriginalFilename().replaceAll("\\s+", "_"));
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        jsonArray = FormUtils.preProcessSampleSheetImport(f, user, sampleService);
      }
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      out.println("<input type='hidden' id='uploadresponsebody' value='" + jsonArray + "'/>");
    } catch (Exception e) {
      log.error("upload sample sheet", e);
    }
  }

  @RequestMapping(value = "/importexport/librarypoolsheet", method = RequestMethod.POST)
  public void uploadLibraryPoolSheet(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      JSONObject result = new JSONObject();
      for (MultipartFile fileItem : getMultipartFiles(request)) {
        uploadFile(Library.class, "forms", fileItem);
        File f = filesManager.getFile(Library.class, "forms", fileItem.getOriginalFilename().replaceAll("\\s+", "_"));
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        result = FormUtils.preProcessLibraryPoolSheetImport(f, user, sampleService);
      }

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      out.println("<input type='hidden' id='uploadresponsebody' value='" + result + "'/>");
    }

    catch (Exception e) {
      log.error("upload library pool sheet", e);
    }
  }

  @RequestMapping(value = "/libraryqc", method = RequestMethod.POST)
  public void uploadLibraryQcDocument(MultipartHttpServletRequest request) throws IOException {
    String libraryId = request.getParameter("libraryId");
    if (libraryId == null) {
      throw new IOException("Cannot upload file - libraryId parameter missing or null");
    } else if (libraryService.get(Long.valueOf(libraryId)) == null) {
      throw new IOException("Cannot upload file - library does not exist");
    }

    for (MultipartFile fileItem : getMultipartFiles(request)) {
      uploadFile(LibraryQC.class, libraryId + File.separator + "qc" + File.separator, fileItem);
    }
  }

  @RequestMapping(value = "/sampleqc", method = RequestMethod.POST)
  public void uploadSampleQcDocument(MultipartHttpServletRequest request) throws IOException {
    String sampleId = request.getParameter("sampleId");
    if (sampleId == null) {
      throw new IOException("Cannot upload file - sampleId parameter missing or null");
    } else if (requestManager.getSampleById(Long.valueOf(sampleId)) == null) {
      throw new IOException("Cannot upload file - sample does not exist");
    }

    for (MultipartFile fileItem : getMultipartFiles(request)) {
      uploadFile(SampleQC.class, sampleId + File.separator + "qc" + File.separator, fileItem);
    }
  }

  @RequestMapping(value = "/dilution-to-pool", method = RequestMethod.POST)
  public void uploadLibraryList(MultipartHttpServletRequest request) throws IOException {
    try {
      HashSet<String> librarySet = new HashSet<>();
      for (MultipartFile fileItem : getMultipartFiles(request)) {
        for (String s : new String(fileItem.getBytes()).split("\n")) {
          librarySet.add(s);
        }
      }

      JSONArray a = JSONArray.fromObject(librarySet);
      JSONObject o = new JSONObject();
      o.put("barcodes", a);
      log.debug(o.toString());

      request.getSession(false).setAttribute("barcodes", o);
    } catch (Exception e) {
      log.debug("UPLOAD FAIL:", e);
    }
  }

  @RequestMapping(value = "/servicerecord", method = RequestMethod.POST)
  public void uploadServiceRecordDocument(MultipartHttpServletRequest request) throws IOException {
    String recordId = request.getParameter("serviceRecordId");
    if (recordId == null) {
      throw new IOException("Cannot upload file - serviceRecordId parameter missing or null");
    } else if (requestManager.getSequencerServiceRecordById(Long.valueOf(recordId)) == null) {
      throw new IOException("Cannot upload file - service record does not exist");
    }

    for (MultipartFile fileItem : getMultipartFiles(request)) {
      uploadFile(SequencerServiceRecord.class, recordId, fileItem);
    }
  }

  private List<MultipartFile> getMultipartFiles(MultipartHttpServletRequest request) {
    List<MultipartFile> files = new ArrayList<>();
    Map<String, MultipartFile> fMap = request.getFileMap();
    for (String fileName : fMap.keySet()) {
      MultipartFile fileItem = fMap.get(fileName);
      if (fileItem.getSize() > 0) {
        files.add(fileItem);
      }
    }
    return files;
  }
}
