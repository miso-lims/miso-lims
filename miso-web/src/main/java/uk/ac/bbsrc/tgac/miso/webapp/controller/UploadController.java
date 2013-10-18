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

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatePool;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.util.FormUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.manager.FilesManager;
import uk.ac.bbsrc.tgac.miso.core.util.jackson.SampleRecursionAvoidanceMixin;
import uk.ac.bbsrc.tgac.miso.webapp.service.forms.MisoFormsService;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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
  private MisoNamingScheme<Library> libraryNamingScheme;

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

  public void setLibraryNamingScheme(MisoNamingScheme<Library> libraryNamingScheme) {
    this.libraryNamingScheme = libraryNamingScheme;
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

  @RequestMapping(value = "/project/bulk-input-form", method = RequestMethod.POST)
  public void uploadProjectBulkInputForm(MultipartHttpServletRequest request) throws IOException {
    String projectId = request.getParameter("projectId");

    try {
      for (MultipartFile fileItem : getMultipartFiles(request)) {
        uploadFile(Project.class, projectId, fileItem);
        File f = filesManager.getFile(Project.class, projectId, fileItem.getOriginalFilename().replaceAll("\\s+", "_"));
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Sample> samples = FormUtils.importSampleInputSpreadsheet(f, user, requestManager, libraryNamingScheme);

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
    }
    catch (Exception e) {
      log.error("SAMPLE IMPORT FAIL:" + e.getMessage());
      //JSONObject o = new JSONObject();
      //o.put("bulkerror", "Cannot import bulk spreadsheet: " + e.getMessage());
      //log.error(o.toString());
      //request.getSession(false).setAttribute("bulkerror", o);
    }
  }

  @RequestMapping(value = "/project/plate-form", method = RequestMethod.POST)
  public void uploadProjectPlateInputForm(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
    String projectId = request.getParameter("projectId");

    try {
      JSONArray a = new JSONArray();
      JSONObject o = new JSONObject();

      for (MultipartFile fileItem : getMultipartFiles(request)) {
        uploadFile(Project.class, projectId, fileItem);
        File f = filesManager.getFile(Project.class, projectId, fileItem.getOriginalFilename().replaceAll("\\s+", "_"));
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        //Map<String, Pool<Plate<LinkedList<Library>, Library>>> pooledPlates = FormUtils.importPlateInputSpreadsheet(f, user, requestManager, libraryNamingScheme);
        Map<String, PlatePool> pooledPlates = FormUtils.importPlateInputSpreadsheet(f, user, requestManager, libraryNamingScheme);

        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Sample.class, SampleRecursionAvoidanceMixin.class);

        String s = mapper.writerWithType(new TypeReference<Collection<PlatePool>>() {})
                         .writeValueAsString(pooledPlates.values());
        a.add(JSONArray.fromObject(s));
      }
      o.put("pools", a);
      log.info(o.toString());

      response.setContentType("text/html");

      PrintWriter out = response.getWriter();
      out.println("<input type='hidden' id='uploadresponsebody' value='"+o.toString()+"'/>");
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  @RequestMapping(value = "/plate/plate-form", method = RequestMethod.POST)
  public void uploadPlateInputForm(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      JSONArray a = new JSONArray();
      JSONObject o = new JSONObject();

      for (MultipartFile fileItem : getMultipartFiles(request)) {
        uploadFile(Plate.class, "forms", fileItem);
        File f = filesManager.getFile(Plate.class, "forms", fileItem.getOriginalFilename().replaceAll("\\s+", "_"));
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        //Map<String, Pool<Plate<LinkedList<Library>, Library>>> pooledPlates = FormUtils.importPlateInputSpreadsheet(f, user, requestManager, libraryNamingScheme);
        Map<String, PlatePool> pooledPlates = FormUtils.importPlateInputSpreadsheet(f, user, requestManager, libraryNamingScheme);

        ObjectMapper mapper = new ObjectMapper();
        mapper.getSerializationConfig().addMixInAnnotations(Sample.class, SampleRecursionAvoidanceMixin.class);

        String s = mapper.writerWithType(new TypeReference<Collection<PlatePool>>() {})
                         .writeValueAsString(pooledPlates.values());
        a.add(JSONArray.fromObject(s));
      }
      o.put("pools", a);
      log.info(o.toString());

      response.setContentType("text/html");

      PrintWriter out = response.getWriter();
      out.println("<input type='hidden' id='uploadresponsebody' value='"+o.toString()+"'/>");
    }
    catch (Exception e) {
      e.printStackTrace();
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

  @RequestMapping(value = "/dilution-to-pool", method = RequestMethod.POST)
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