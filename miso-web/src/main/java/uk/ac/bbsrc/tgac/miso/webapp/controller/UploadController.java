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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.util.FormUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.webapp.service.forms.MisoFormsService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/upload")
public class UploadController {
  protected static final Logger log = LoggerFactory.getLogger(UploadController.class);

  @Autowired
  public FilesManager filesManager;
  @Autowired
  public MisoFormsService misoFormsService;

  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }
  public void setMisoFormsService(MisoFormsService misoFormsService) {
    this.misoFormsService = misoFormsService;
  }

  private Class lookupCoreClass(String className) throws ClassNotFoundException {
    return this.getClass().getClassLoader().loadClass("uk.ac.bbsrc.tgac.miso.core."+className);
  }

  public void uploadFile(Class type, String qualifier, MultipartFile fileItem) throws IOException {
    File dir = new File(filesManager.getFileStorageDirectory()+File.separator+type.getSimpleName().toLowerCase()+File.separator+qualifier);
    if (LimsUtils.checkDirectory(dir, true)) {
      log.info("Attempting to store " + dir.toString() + File.separator + fileItem.getOriginalFilename());
      fileItem.transferTo(new File(dir+File.separator+fileItem.getOriginalFilename().replaceAll("\\s", "_")));
    }
    else {
      throw new IOException("Cannot upload file - check that the directory specified in miso.properties exists and is writable");
    }
  }

  public void uploadFile(Object type, String qualifier, MultipartFile fileItem) throws IOException {
    uploadFile(type.getClass(), qualifier, fileItem);
  }


  @RequestMapping(value = "/project", method = RequestMethod.POST)
  public void uploadProjectDocument(MultipartHttpServletRequest request) throws IOException {
    String projectId = request.getParameter("projectId");

    for (MultipartFile fileItem : getMultipartFiles(request)) {
      uploadFile(Project.class, projectId, fileItem);
    }
  }

  @RequestMapping(value = "/project/sample-delivery-form", method = RequestMethod.POST)
  public String uploadProjectSampleDeliveryForm(MultipartHttpServletRequest request) throws IOException {
    String projectId = request.getParameter("projectId");

    boolean taxonCheck = (Boolean)request.getSession().getServletContext().getAttribute("taxonLookupEnabled");

    try {
      for (MultipartFile fileItem : getMultipartFiles(request)) {
        uploadFile(Project.class, projectId, fileItem);
        File f = filesManager.getFile(Project.class, projectId, fileItem.getOriginalFilename().replaceAll("\\s+", "_"));
        List<Sample> samples = FormUtils.importSampleDeliveryForm(f);
        log.info("Importing samples from form: " + samples.toString());
        misoFormsService.importSampleDeliveryFormSamples(samples, taxonCheck);
      }
      return "redirect:/miso/project/" + projectId;
    }
    catch (Exception e) {
      log.error("SAMPLE IMPORT FAIL:", e);
      throw new IOException(e);
    }
  }

  @RequestMapping(value = "/libraryqc", method = RequestMethod.POST)
  public void uploadLibraryQcDocument(MultipartHttpServletRequest request) throws IOException {
    String libraryId = request.getParameter("libraryId");

    for (MultipartFile fileItem : getMultipartFiles(request)) {
      uploadFile(LibraryQC.class, libraryId+File.separator+"qc"+File.separator, fileItem);
    }
  }

  @RequestMapping(value = "/sampleqc", method = RequestMethod.POST)
  public void uploadSampleQcDocument(MultipartHttpServletRequest request) throws IOException {
    String sampleId = request.getParameter("sampleId");

    for (MultipartFile fileItem : getMultipartFiles(request)) {
      uploadFile(SampleQC.class, sampleId+File.separator+"qc"+File.separator, fileItem);
    }
  }

  @RequestMapping(value = "/librarydilution-to-pool", method = RequestMethod.POST)
  public void uploadLibraryList(MultipartHttpServletRequest request) throws IOException {
    try {
      HashSet<String> librarySet = new HashSet<String>();
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
    }
    catch (Exception e) {
      log.debug("UPLOAD FAIL:", e);
      //return JSONUtils.SimpleJSONError("Upload failed: "+e.getMessage());
    }
  }

  private List<MultipartFile> getMultipartFiles(MultipartHttpServletRequest request) {
    List<MultipartFile> files = new ArrayList<MultipartFile>();
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