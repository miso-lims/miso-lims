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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.opensymphony.util.FileUtils;
import net.sf.ehcache.Cache;
import net.sf.json.JSONArray;
import org.apache.commons.codec.binary.Base64;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.ProgressType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoNamingException;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.service.naming.MisoNamingScheme;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.util.*;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.MisoJscriptFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.sqlstore.util.DbUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class SampleControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(SampleControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private DataObjectFactory dataObjectFactory;
  @Autowired
  private BarcodeFactory barcodeFactory;
  @Autowired
  private PrintManager<MisoPrintService, Queue<?>> printManager;
  @Autowired
  private MisoNamingScheme<Sample> sampleNamingScheme;
  @Autowired
  private CacheHelperService cacheHelperService;

  public JSONObject validateSampleAlias(HttpSession session, JSONObject json) {
    if (json.has("alias")) {
      String alias = json.getString("alias");
      try {
        if (sampleNamingScheme.validateField("alias", alias)) {
          log.info("Sample alias OK!");
          return JSONUtils.SimpleJSONResponse("OK");
        }
        else {
          log.error("Sample alias not valid: " + alias);
          return JSONUtils.SimpleJSONError("The following sample alias doesn't conform to the chosen naming scheme (" + sampleNamingScheme.getValidationRegex("alias") + ") or already exists: " + json.getString("alias"));
        }
      }
      catch (MisoNamingException e) {
        log.error("Cannot validate sample alias " + json.getString("alias") + ": " + e.getMessage());
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Cannot validate sample alias " + json.getString("alias") + ": " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No alias specified");
    }
  }

  public JSONObject bulkSaveSamples(HttpSession session, JSONObject json) {
    if (json.has("samples")) {
      try {
        Project p = requestManager.getProjectById(json.getLong("projectId"));
        SecurityProfile sp = p.getSecurityProfile();
        JSONArray a = JSONArray.fromObject(json.get("samples"));
        Set<Sample> saveSet = new HashSet<Sample>();

        for (JSONObject j : (Iterable<JSONObject>) a) {
          try {
            String alias = j.getString("alias");

            if (sampleNamingScheme.validateField("alias", alias)) {
              String descr = j.getString("description");
              String scientificName = j.getString("scientificName");
              DateFormat df = new SimpleDateFormat("dd/mm/yyyy");
              String type = j.getString("sampleType");
              String locationBarcode = j.getString("locationBarcode");

              Sample news = new SampleImpl();
              news.setProject(p);
              news.setAlias(alias);
              news.setDescription(descr);
              news.setScientificName(scientificName);
              news.setSecurityProfile(sp);
              news.setSampleType(type);
              news.setLocationBarcode(locationBarcode);

              if (j.has("receivedDate") && !"".equals(j.getString("receivedDate"))) {
                Date date = df.parse(j.getString("receivedDate"));
                news.setReceivedDate(date);
              }

              if (!j.getString("note").equals("")) {
                Note note = new Note();
                note.setOwner(sp.getOwner());
                note.setText(j.getString("note"));
                note.setInternalOnly(true);

                if (j.has("receivedDate") && !"".equals(j.getString("receivedDate"))) {
                  Date date = df.parse(j.getString("receivedDate"));
                  note.setCreationDate(date);
                }
                else {
                  note.setCreationDate(new Date());
                }

                news.setNotes(Arrays.asList(note));
              }

              saveSet.add(news);
            }
            else {
              return JSONUtils.SimpleJSONError("The following sample alias doesn't conform to the chosen naming scheme (" + sampleNamingScheme.getValidationRegex("alias") + ") or already exists: " + j.getString("alias"));
            }
          }
          catch (ParseException e) {
            e.printStackTrace();
            return JSONUtils.SimpleJSONError("Cannot parse date for sample " + j.getString("alias"));
          }
          catch (MisoNamingException e) {
            e.printStackTrace();
            return JSONUtils.SimpleJSONError("Cannot validate sample alias " + j.getString("alias") + ": " + e.getMessage());
          }
        }

        Set<Sample> samples = new HashSet<Sample>(requestManager.listAllSamples());
        // relative complement to find objects that aren't already persisted
        Set<Sample> complement = LimsUtils.relativeComplementByProperty(
            Sample.class,
            "getAlias",
            saveSet,
            samples);

        if (complement != null && !complement.isEmpty()) {
          List<Sample> sortedList = new ArrayList<Sample>(complement);
          List<String> savedSamples = new ArrayList<String>();
          List<String> taxonErrorSamples = new ArrayList<String>();
          Collections.sort(sortedList, new AliasComparator(Sample.class));

          for (Sample sample : sortedList) {
            if ((Boolean) session.getServletContext().getAttribute("taxonLookupEnabled")) {
              log.info("Checking taxon: " + sample.getScientificName());
              String taxon = TaxonomyUtils.checkScientificNameAtNCBI(sample.getScientificName());
              if (taxon != null) {
                sample.setTaxonIdentifier(taxon);
                taxonErrorSamples.add(sample.getAlias());
              }
            }

            try {
              requestManager.saveSample(sample);
              savedSamples.add(sample.getAlias());
              log.info("Saved: " + sample.getAlias());
            }
            catch (IOException e) {
              log.error("Couldn't save: " + sample.getAlias());
              e.printStackTrace();
            }
          }

          Map<String, Object> response = new HashMap<String, Object>();
          response.put("savedSamples", JSONArray.fromObject(savedSamples));
          response.put("taxonErrorSamples", JSONArray.fromObject(taxonErrorSamples));

          return JSONUtils.JSONObjectResponse(response);
        }
        else {
          return JSONUtils.SimpleJSONError("Error in saving samples - perhaps samples specified already exist in the database with a given alias?");
        }
      }
      catch (NoSuchMethodException e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Cannot save samples for project " + json.getLong("projectId") + ": " + e.getMessage());
      }
      catch (IOException e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Cannot save samples for project " + json.getLong("projectId") + ": " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No samples specified");
    }
  }

  public JSONObject getSampleQCUsers(HttpSession session, JSONObject json) {
    try {
      Collection<String> users = new HashSet<String>();
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      users.add(user.getFullName());

      if (json.has("sampleId") && !json.get("sampleId").equals("")) {
        Long sampleId = Long.parseLong(json.getString("sampleId"));
        Sample sample = requestManager.getSampleById(sampleId);

        Project p = sample.getProject();
        if (p.userCanRead(user)) {
          for (ProjectOverview po : p.getOverviews()) {
            if (po.getSampleGroup() != null) {
              if (po.getSampleGroup().getEntities().contains(sample)) {
                users.add(po.getPrincipalInvestigator());
              }
            }
          }
        }
      }
      StringBuilder sb = new StringBuilder();
      for (String name : users) {
        sb.append("<option value='" + name + "'>" + name + "</option>");
      }
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("qcUserOptions", sb.toString());
      map.put("sampleId", json.getString("sampleId"));
      return JSONUtils.JSONObjectResponse(map);
    }
    catch (IOException e) {
      log.error("Failed to get available users for this Sample QC: ", e);
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to get available users for this Sample QC: " + e.getMessage());
    }
  }

  public JSONObject getSampleQcTypes(HttpSession session, JSONObject json) {
    try {
      StringBuilder sb = new StringBuilder();
      Collection<QcType> types = requestManager.listAllSampleQcTypes();
      for (QcType s : types) {
        sb.append("<option units='" + s.getUnits() + "' value='" + s.getQcTypeId() + "'>" + s.getName() + "</option>");
      }
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("types", sb.toString());
      return JSONUtils.JSONObjectResponse(map);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return JSONUtils.SimpleJSONError("Cannot list all Sample QC Types");
  }

  public JSONObject addSampleQC(HttpSession session, JSONObject json) {
    try {
      for (Object key : json.keySet()) {
        if (json.get(key) == null || json.get(key).equals("")) {
          String k = (String) key;
          return JSONUtils.SimpleJSONError("Please enter a value for '" + k + "'");
        }
      }
      if (json.has("sampleId") && !json.get("sampleId").equals("")) {
        Long sampleId = Long.parseLong(json.getString("sampleId"));
        Sample sample = requestManager.getSampleById(sampleId);
        if (json.get("qcPassed") != null) {
          if ("true".equals(json.getString("qcPassed"))) {
            sample.setQcPassed(true);
          }
          else if ("false".equals(json.getString("qcPassed"))) {
            sample.setQcPassed(false);
          }
        }

        SampleQC newQc = dataObjectFactory.getSampleQC();
        newQc.setQcCreator(json.getString("qcCreator"));
        newQc.setQcDate(new SimpleDateFormat("dd/MM/yyyy").parse(json.getString("qcDate")));
        newQc.setQcType(requestManager.getSampleQcTypeById(json.getLong("qcType")));
        newQc.setResults(Double.parseDouble(json.getString("results")));
        sample.addQc(newQc);
        requestManager.saveSampleQC(newQc);

        StringBuilder sb = new StringBuilder();
        sb.append("<tr><th>QCed By</th><th>QC Date</th><th>Method</th><th>Results</th></tr>");
        for (SampleQC qc : sample.getSampleQCs()) {
          sb.append("<tr>");
          sb.append("<td>" + qc.getQcCreator() + "</td>");
          sb.append("<td>" + qc.getQcDate() + "</td>");
          sb.append("<td>" + qc.getQcType().getName() + "</td>");
          sb.append("<td>" + qc.getResults() + " " + qc.getQcType().getUnits() + "</td>");
          sb.append("</tr>");
        }
        return JSONUtils.SimpleJSONResponse(sb.toString());
      }
    }
    catch (Exception e) {
      log.error("Failed to add Sample QC to this sample: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Sample QC to this sample: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add SampleQC");
  }


  public JSONObject changeSampleQCRow(HttpSession session, JSONObject json) {
    try {
      JSONObject response = new JSONObject();
      Long qcId = Long.parseLong(json.getString("qcId"));
      SampleQC sampleQc = requestManager.getSampleQCById(qcId);
      response.put("results", "<input type='text' id='" + qcId + "' value='" + sampleQc.getResults() + "'/>");
      response.put("edit", "<a href='javascript:void(0);' onclick='Sample.qc.editSampleQC(\"" + qcId + "\");'>Save</a>");
      return response;
    }
    catch (Exception e) {
      log.error("Failed to display Sample QC of this sample: ", e);
      return JSONUtils.SimpleJSONError("Failed to display Sample QC of this sample: " + e.getMessage());
    }
  }

  public JSONObject editSampleQC(HttpSession session, JSONObject json) {
    try {
      if (json.has("qcId") && !json.get("qcId").equals("")) {
        Long qcId = Long.parseLong(json.getString("qcId"));
        SampleQC sampleQc = requestManager.getSampleQCById(qcId);
        sampleQc.setResults(Double.parseDouble(json.getString("result")));
        requestManager.saveSampleQC(sampleQc);
        return JSONUtils.SimpleJSONResponse("OK");
      }
    }
    catch (Exception e) {
      log.error("Failed to add Sample QC to this sample: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Sample QC to this sample: " + e.getMessage());
    }
    return JSONUtils.SimpleJSONError("Cannot add SampleQC");
  }

  public JSONObject bulkAddSampleQCs(HttpSession session, JSONObject json) {
    try {
      JSONArray qcs = JSONArray.fromObject(json.getString("qcs"));
      //validate
      boolean ok = true;
      for (JSONObject qc : (Iterable<JSONObject>) qcs) {
        String qcType = qc.getString("qcType");
        String results = qc.getString("results");
        String qcCreator = qc.getString("qcCreator");
        String qcDate = qc.getString("qcDate");

        if (qcType == null || qcType.equals("") ||
            results == null || results.equals("") ||
            qcCreator == null || qcCreator.equals("") ||
            qcDate == null || qcDate.equals("")) {
          ok = false;
        }
      }

      //persist
      if (ok) {
        Map<String, Object> map = new HashMap<String, Object>();
        JSONArray a = new JSONArray();
        JSONArray errors = new JSONArray();
        for (JSONObject qc : (Iterable<JSONObject>) qcs) {
          JSONObject j = addSampleQC(session, qc);
          j.put("sampleId", qc.getString("sampleId"));
          if (j.has("error")) {
            errors.add(j);
          }
          else {
            a.add(j);
          }
        }
        map.put("saved", a);
        if (!errors.isEmpty()) {
          map.put("errors", errors);
        }
        return JSONUtils.JSONObjectResponse(map);
      }
      else {
        log.error("Failed to add Sample QC to this Library: one of the required fields of the selected QCs is missing or invalid");
        return JSONUtils.SimpleJSONError("Failed to add Sample QC to this Library: one of the required fields of the selected QCs is missing or invalid");
      }
    }
    catch (Exception e) {
      log.error("Failed to add Sample QC to this sample: ", e);
      return JSONUtils.SimpleJSONError("Failed to add Sample QC to this sample: " + e.getMessage());
    }
  }

  public JSONObject addSampleNote(HttpSession session, JSONObject json) {
    Long sampleId = json.getLong("sampleId");
    String internalOnly = json.getString("internalOnly");
    String text = json.getString("text");

    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
      Sample sample = requestManager.getSampleById(sampleId);
      Note note = new Note();

      internalOnly = internalOnly.equals("on") ? "true" : "false";

      note.setInternalOnly(Boolean.parseBoolean(internalOnly));
      note.setText(text);
      note.setOwner(user);
      note.setCreationDate(new Date());
      sample.getNotes().add(note);
      requestManager.saveSampleNote(sample, note);
      requestManager.saveSample(sample);
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject deleteSampleNote(HttpSession session, JSONObject json) {
    Long sampleId = json.getLong("sampleId");
    Long noteId = json.getLong("noteId");

    try {
      Sample sample = requestManager.getSampleById(sampleId);
      Note note = requestManager.getNoteById(noteId);
      if (sample.getNotes().contains(note)) {
        sample.getNotes().remove(note);
        requestManager.deleteNote(note);
        requestManager.saveSample(sample);
        return JSONUtils.SimpleJSONResponse("OK");
      }
      else {
        return JSONUtils.SimpleJSONError("Sample does not have note " + noteId + ". Cannot remove");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Cannot remove note: " + e.getMessage());
    }
  }

  public JSONObject getSampleByBarcode(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    String barcode = json.getString("barcode");
    if (LimsUtils.isBase64String(barcode)) {
      log.info(barcode + "is base64");
      //Base64-encoded string, most likely a barcode image beeped in. decode and search
      barcode = new String(Base64.decodeBase64(barcode));
    }

    try {
      Sample sample = requestManager.getSampleByBarcode(barcode);
      if (sample.getReceivedDate() == null) {
        response.put("name", sample.getName());
        response.put("desc", sample.getDescription());
        response.put("id", sample.getId());
        response.put("type", sample.getSampleType());
        response.put("project", sample.getProject().getName());
        return response;
      }
      else {
        return JSONUtils.SimpleJSONError("Sample " + sample.getName() + " has already been received");
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage() + ": This sample doesn't seem to be in the database.");
    }
  }

  public JSONObject setSampleReceivedDateByBarcode(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    JSONArray ss = JSONArray.fromObject(json.getString("samples"));

    try {
      for (JSONObject s : (Iterable<JSONObject>) ss) {
        Long sampleId = s.getLong("sampleId");
        Sample sample = requestManager.getSampleById(sampleId);
        sample.setReceivedDate(new Date());
        requestManager.saveSample(sample);
      }
      response.put("result", "Samples received date saved");
      return response;
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot set receipt date for sample");
    }
  }

  public JSONObject getSampleBarcode(HttpSession session, JSONObject json) {
    Long sampleId = json.getLong("sampleId");
    File temploc = new File(session.getServletContext().getRealPath("/") + "temp/");
    try {
      Sample sample = requestManager.getSampleById(sampleId);
      barcodeFactory.setPointPixels(1.5f);
      barcodeFactory.setBitmapResolution(600);
      RenderedImage bi = null;

      if (json.has("barcodeGenerator")) {
        BarcodeDimension dim = new BarcodeDimension(100, 100);
        if (json.has("dimensionWidth") && json.has("dimensionHeight")) {
          dim = new BarcodeDimension(json.getDouble("dimensionWidth"), json.getDouble("dimensionHeight"));
        }
        BarcodeGenerator bg = BarcodeFactory.lookupGenerator(json.getString("barcodeGenerator"));
        if (bg != null) {
          bi = barcodeFactory.generateBarcode(sample, bg, dim);
        }
        else {
          return JSONUtils.SimpleJSONError("'" + json.getString("barcodeGenerator") + "' is not a valid barcode generator type");
        }
      }
      else {
        bi = barcodeFactory.generateSquareDataMatrix(sample, 400);
      }

      if (bi != null) {
        File tempimage = misoFileManager.generateTemporaryFile("barcode-", ".png", temploc);
        if (ImageIO.write(bi, "png", tempimage)) {
          return JSONUtils.JSONObjectResponse("img", tempimage.getName());
        }
        return JSONUtils.SimpleJSONError("Writing temp image file failed.");
      }
      else {
        return JSONUtils.SimpleJSONError("Sample has no parseable barcode");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot seem to access " + temploc.getAbsolutePath());
    }
  }

  public JSONObject printSampleBarcodes(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String serviceName = null;
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(Sample.class);
        if (services.size() == 1) {
          mps = services.iterator().next();
          if (mps == null) {
            return JSONUtils.SimpleJSONError("Unable to resolve a print service for Samples. A service seems to be recognised but cannot be resolved.");
          }
        }
        else {
          return JSONUtils.SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      }
      else {
        mps = printManager.getPrintService(serviceName);
        if (mps == null) {
          return JSONUtils.SimpleJSONError("Unable to resolve a print service for Samples with the name '" + serviceName + "'.");
        }
      }

      Queue<File> thingsToPrint = new LinkedList<File>();
      JSONArray ss = JSONArray.fromObject(json.getString("samples"));
      for (JSONObject s : (Iterable<JSONObject>) ss) {
        try {
          Long sampleId = s.getLong("sampleId");
          Sample sample = requestManager.getSampleById(sampleId);
          //autosave the barcode if none has been previously generated
          if (sample.getIdentificationBarcode() == null || "".equals(sample.getIdentificationBarcode())) {
            requestManager.saveSample(sample);
          }
          File f = mps.getLabelFor(sample);
          if (f != null) thingsToPrint.add(f);
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Error printing barcodes: " + e.getMessage());
        }
      }

      PrintJob pj = printManager.print(thingsToPrint, mps.getName(), user);
      return JSONUtils.SimpleJSONResponse("Job " + pj.getJobId() + " : Barcodes printed.");
    }
    catch (MisoPrintException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to print barcodes: " + e.getMessage());
    }
  }

  public JSONObject changeSampleLocation(HttpSession session, JSONObject json) {
    Long sampleId = json.getLong("sampleId");
    String locationBarcode = json.getString("locationBarcode");

    try {
      String newLocation = LimsUtils.lookupLocation(locationBarcode);
      if (!"".equals(newLocation)) {
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        Sample sample = requestManager.getSampleById(sampleId);
        String oldLocation = sample.getLocationBarcode();
        sample.setLocationBarcode(newLocation);

        Note note = new Note();
        note.setInternalOnly(true);
        note.setText("Location changed from " + oldLocation + " to " + newLocation + " by " + user.getLoginName() + " on " + new Date());
        note.setOwner(user);
        note.setCreationDate(new Date());
        sample.getNotes().add(note);
        requestManager.saveSampleNote(sample, note);
        requestManager.saveSample(sample);
      }
      else {
        return JSONUtils.SimpleJSONError("New location barcode not recognised");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Note saved successfully");
  }

  public JSONObject lookupNCBIScientificName(HttpSession session, JSONObject json) {
    String taxon = TaxonomyUtils.checkScientificNameAtNCBI(json.getString("scientificName"));
    if (taxon != null) {
      return JSONUtils.SimpleJSONResponse("NCBI taxon is valid");
    }
    else {
      return JSONUtils.SimpleJSONError("This scientific name is not of a known taxonomy. You may have problems when trying to submit this data to public repositories.");
    }
  }

  public JSONObject deleteSample(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("sampleId")) {
        Long sampleId = json.getLong("sampleId");
        try {
          requestManager.deleteSample(requestManager.getSampleById(sampleId));
          return JSONUtils.SimpleJSONResponse("Sample deleted");
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete sample: " + e.getMessage());
        }
      }
      else {
        return JSONUtils.SimpleJSONError("No sample specified to delete.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }

  public JSONObject removeSampleFromGroup(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null) {
      if (json.has("sampleId") && json.has("sampleGroupId")) {
        Long sampleId = json.getLong("sampleId");
        Long sampleGroupId = json.getLong("sampleGroupId");
        try {
          Sample s = requestManager.getSampleById(sampleId);
          EntityGroup<? extends Nameable, ? extends Nameable> osg = requestManager.getEntityGroupById(sampleGroupId);
          if (osg.getEntities().contains(s)) {
            if (osg.getEntities().remove(s)) {
              requestManager.saveEntityGroup(osg);

              cacheHelperService.evictObjectFromCache(s.getProject(), Project.class);
              return JSONUtils.SimpleJSONResponse("Sample removed from group");
            }
            else {
              return JSONUtils.SimpleJSONError("Error removing sample from sample group.");
            }
          }
          else {
            return JSONUtils.SimpleJSONResponse("Sample not in this sample group!");
          }
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot remove sample from group: " + e.getMessage());
        }
      }
      else {
        return JSONUtils.SimpleJSONError("No sample or sample group specified to remove.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Only logged-in users can remove objects.");
    }
  }

  public JSONObject listSamplesDataTable(HttpSession session, JSONObject json) {
    try {
      JSONObject j = new JSONObject();
      JSONArray jsonArray = new JSONArray();
      for (Sample sample : requestManager.listAllSamples()) {
        jsonArray.add("['" + 
                      TableHelper.hyperLinkify("/miso/sample/" + sample.getId(), 
                                                sample.getName(), true) + "','" +
                      TableHelper.hyperLinkify("/miso/sample/" + sample.getId(), 
                                                sample.getAlias()) + "','" + 
                      sample.getSampleType() + "','" +
                      (sample.getQcPassed() != null ? sample.getQcPassed().toString() : "") + "','" +
                      getSampleLastQC(sample.getId()) + "']");
      }
      j.put("array", jsonArray);
      return j;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject getSampleLastQCRequest(HttpSession session, JSONObject json) {
    Long sampleId = json.getLong("sampleId");
    return JSONUtils.SimpleJSONResponse(getSampleLastQC(sampleId));
  }

  public String getSampleLastQC(Long sampleId){
    try {
     String sampleQCValue = "NA";
     Collection<SampleQC> sampleQCs =  requestManager.listAllSampleQCsBySampleId(sampleId);
      if (sampleQCs.size()>0){
        List<SampleQC> list = new ArrayList(sampleQCs);
        Collections.sort(list, new Comparator<SampleQC>() {
          public int compare(SampleQC sqc1, SampleQC sqc2) {
            return (int) sqc1.getId() - (int) sqc2.getId();
          }
        });
        SampleQC sampleQC = list.get(list.size()-1);
        sampleQCValue = sampleQC.getResults().toString();
      }
      return sampleQCValue;
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return "Failed: " + e.getMessage();
    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }

  public void setBarcodeFactory(BarcodeFactory barcodeFactory) {
    this.barcodeFactory = barcodeFactory;
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  public void setPrintManager(PrintManager<MisoPrintService, Queue<?>> printManager) {
    this.printManager = printManager;
  }

  public void setSampleNamingScheme(MisoNamingScheme<Sample> sampleNamingScheme) {
    this.sampleNamingScheme = sampleNamingScheme;
  }

  public void setCacheHelperService(CacheHelperService cacheHelperService) {
    this.cacheHelperService = cacheHelperService;
  }
}
