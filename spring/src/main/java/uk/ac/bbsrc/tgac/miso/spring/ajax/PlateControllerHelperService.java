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

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.Barcodable;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Plateable;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatePool;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlateMaterialType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.util.FormUtils;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Rob Davey
 * @since 0.1.2
 */
@Ajaxified
public class PlateControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(PlateControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private MisoFilesManager misoFileManager;
  @Autowired
  private BarcodeFactory barcodeFactory;
  @Autowired
  private PrintManager<MisoPrintService, Queue<?>> printManager;

  public JSONObject getPlateBarcode(HttpSession session, JSONObject json) {
    Long plateId = json.getLong("plateId");
    File temploc = new File(session.getServletContext().getRealPath("/") + "temp/");
    try {
      //Plate<LinkedList<Plateable>, Plateable> plate = requestManager.<LinkedList<Plateable>, Plateable> getPlateById(plateId);
      Plate<? extends List<? extends Plateable>, ? extends Plateable> plate = requestManager.getPlateById(plateId);
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
          bi = barcodeFactory.generateBarcode(plate, bg, dim);
        }
        else {
          return JSONUtils.SimpleJSONError("'" + json.getString("barcodeGenerator") + "' is not a valid barcode generator type");
        }
      }
      else {
        bi = barcodeFactory.generateSquareDataMatrix(plate, 400);
      }

      if (bi != null) {
        File tempimage = misoFileManager.generateTemporaryFile("barcode-", ".png", temploc);
        if (ImageIO.write(bi, "png", tempimage)) {
          return JSONUtils.JSONObjectResponse("img", tempimage.getName());
        }
        return JSONUtils.SimpleJSONError("Writing temp image file failed.");
      }
      else {
        return JSONUtils.SimpleJSONError("Plate has no parseable barcode");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage() + ": Cannot seem to access " + temploc.getAbsolutePath());
    }
  }

  public JSONObject printPlateBarcodes(HttpSession session, JSONObject json) {
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      String serviceName = null;
      if (json.has("serviceName")) {
        serviceName = json.getString("serviceName");
      }

      MisoPrintService<File, Barcodable, PrintContext<File>> mps = null;
      if (serviceName == null) {
        Collection<MisoPrintService> services = printManager.listPrintServicesByBarcodeableClass(Plate.class);
        if (services.size() == 1) {
          mps = services.iterator().next();
        }
        else {
          return JSONUtils.SimpleJSONError("No serviceName specified, but more than one available service able to print this barcode type.");
        }
      }
      else {
        mps = printManager.getPrintService(serviceName);
      }

      Queue<File> thingsToPrint = new LinkedList<File>();
      JSONArray ss = JSONArray.fromObject(json.getString("plates"));
      for (JSONObject s : (Iterable<JSONObject>) ss) {
        try {
          Long plateId = s.getLong("plateId");
          //Plate<LinkedList<Plateable>, Plateable>  plate = requestManager.<LinkedList<Plateable>, Plateable> getPlateById(plateId);
          Plate<? extends List<? extends Plateable>, ? extends Plateable> plate = requestManager.getPlateById(plateId);
          //autosave the barcode if none has been previously generated
          if (plate.getIdentificationBarcode() == null || "".equals(plate.getIdentificationBarcode())) {
            requestManager.savePlate(plate);
          }
          File f = mps.getLabelFor(plate);
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

  public JSONObject changePlateLocation(HttpSession session, JSONObject json) {
    Long plateId = json.getLong("plateId");
    String locationBarcode = json.getString("locationBarcode");

    try {
      String newLocation = LimsUtils.lookupLocation(locationBarcode);
      if (!"".equals(newLocation)) {
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        //Plate<LinkedList<Plateable>, Plateable> plate = requestManager.<LinkedList<Plateable>, Plateable> getPlateById(plateId);
        Plate<? extends List<? extends Plateable>, ? extends Plateable> plate = requestManager.getPlateById(plateId);
        String oldLocation = plate.getLocationBarcode();
        log.debug("Location changing from " + oldLocation + " to " + newLocation);
        plate.setLocationBarcode(newLocation);
        log.debug("Changed location to "+ plate.getLocationBarcode());
        /*
        Note note = new Note();
        note.setInternalOnly(true);
        note.setText("Location changed to " + newLocation + " by " + user.getLoginName() + " on " + new Date());
        note.setOwner(user);
        note.setCreationDate(new Date());
        plate.getNotes().add(note);
        requestManager.saveSampleNote(sample, note);
        */
        requestManager.savePlate(plate);
      }
      else {
        return JSONUtils.SimpleJSONError("New location barcode not recognised");
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError(e.getMessage());
    }

    return JSONUtils.SimpleJSONResponse("Plate saved successfully");
  }

  public JSONObject getTagBarcodesForMaterialType(HttpSession session, JSONObject json) {
    Map<String, Object> responseMap = new HashMap<String, Object>();
    if (json.has("materialType") && !"".equals(json.getString("materialType"))) {
      String materialType = json.getString("materialType");
      StringBuilder srb = new StringBuilder();
      srb.append("<select name='tagBarcode' id='tagBarcodes'>");
      srb.append("<option value='0' selected='selected'>No barcode</option>");
//      for (TagBarcode tb : requestManager.listPlateBarcodesByMaterialType(PlateMaterialType.get(materialType))) {
//        srb.append("<option value='" + tb.getTagBarcodeId() + "'>" + tb.getName() + " ("+ tb.getSequence()+")</option>");
//      }
      srb.append("</select>");

      responseMap.put("plateBarcodes", srb.toString());
    }
    else {
      return JSONUtils.SimpleJSONError("Unrecognised MaterialType");
    }
    return JSONUtils.JSONObjectResponse(responseMap);
  }

  public JSONObject downloadPlateInputForm(HttpSession session, JSONObject json) {
    if (json.has("documentFormat")) {
      String documentFormat = json.getString("documentFormat");
      try {
        File f = misoFileManager.getNewFile(
            Plate.class,
            "forms",
            "PlateInputForm-" + LimsUtils.getCurrentDateAsString() + "." + documentFormat);
        FormUtils.createPlateInputSpreadsheet(f);
        return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
      }
      catch (Exception e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Failed to get plate input form: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Missing project ID or document format supplied.");
    }
  }

  public JSONObject saveImportedElements(HttpSession session, JSONObject json) {
    if (json.has("elements")) {
      Plate currentPlate = null;
      Pool currentPool = null;

      try {
        String description = json.getString("description");
        String creationDate = json.getString("creationDate");
        String plateMaterialType = null;
        if (json.has("plateMaterialType") && !json.getString("plateMaterialType").equals("")) {
          plateMaterialType = json.getString("plateMaterialType");
        }

        JSONObject elements = json.getJSONObject("elements");
        JSONArray pools = JSONArray.fromObject(elements.get("pools"));
        JSONArray savedPlates = new JSONArray();
        ObjectMapper mapper = new ObjectMapper();

        for (JSONArray innerPoolList : (Iterable<JSONArray>) pools) {
          for (JSONObject pool : (Iterable<JSONObject>) innerPoolList) {
            log.info(pool.toString());

            PlatePool platePool = mapper.readValue(pool.toString(), new TypeReference<PlatePool>() {
            });
            currentPool = platePool;

            for (Plate<LinkedList<Library>, Library> plate : platePool.getPoolableElements()) {
              JSONObject j = new JSONObject();

//              if (json.has("tagBarcode")) {
//                String tagBarcode = json.getString("tagBarcode");
//                plate.setTagBarcode(requestManager.listAllTagBarcodesByStrategyName());
//              }

              if (plate.getDescription() == null) {
                plate.setDescription(description);
              }

              if (plate.getCreationDate() == null) {
                //plate.setCreationDate(DateFormat.getInstance().parse(creationDate));
              }

              if (plate.getPlateMaterialType() == null && plateMaterialType != null) {
                plate.setPlateMaterialType(PlateMaterialType.valueOf(plateMaterialType));
              }
              log.info("Saving plate: " + plate.toString());
              currentPlate = plate;
              long plateId = requestManager.savePlate(plate);
              j.put("plateId", plateId);
              savedPlates.add(j);
              currentPlate = null;
            }

            log.info("Saving pool: " + pool.toString());
            requestManager.savePool(platePool);
            currentPool = null;
          }
        }
        JSONObject resp = new JSONObject();
        resp.put("plates", savedPlates);
        return resp;
      }
      catch (IOException e) {
        if (currentPool != null) {
          log.error("Error saving pool elements on new plate save. Deleting pool " + currentPool.toString());
          //clear out child elements to make sure plate meets delete requirements
          currentPool.getPoolableElements().clear();
          try {
            requestManager.deletePool(currentPool);
          }
          catch (IOException e1) {
            log.error("Cannot delete pool. Nothing left to do.");
            e1.printStackTrace();
          }
        }

        if (currentPlate != null) {
          log.error("Error saving plate elements on new plate save. Deleting plate " + currentPlate.toString());
          //clear out child elements to make sure plate meets delete requirements
          currentPlate.getElements().clear();
          try {
            requestManager.deletePlate(currentPlate);
          }
          catch (IOException e1) {
            log.error("Cannot delete plate. Nothing left to do.");
            e1.printStackTrace();
          }
        }

        log.error("Caused by...");
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Cannot save imported plate: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No valid plates available to save");
    }
  }

  public JSONObject plateElementsDataTable(HttpSession session, JSONObject json) {
    if (json.has("plateId")) {
      try {
        JSONObject j = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        long plateId = json.getLong("plateId");

        Plate<? extends List<? extends Plateable>, ? extends Plateable> plate = requestManager.getPlateById(plateId);
        if (plate != null) {
          for (Plateable p : plate.getElements()) {
            if (p instanceof Library) {
              Library l = (Library) p;
              String strategyName = "No barcode";

              StringBuilder seqbuilder = new StringBuilder();
              if (!l.getTagBarcodes().isEmpty()) {
                int count = 1;
                Collection<TagBarcode> barcodes = l.getTagBarcodes().values();
                for (TagBarcode tb : barcodes) {
                  strategyName = tb.getStrategyName();
                  seqbuilder.append(tb.getSequence());
                  if (l.getTagBarcodes().values().size() > 1 && count < l.getTagBarcodes().values().size()) {
                    seqbuilder.append("-");
                  }
                  count++;
                }
              }
              else {
                log.info("No tag barcodes!");
              }

              jsonArray.add("['" +
                            TableHelper.hyperLinkify("/miso/library/" + l.getId(), 
                                                      l.getName(), true) + "','" +
                            TableHelper.hyperLinkify("/miso/library/" + l.getId(), 
                                                      l.getAlias()) + "','" +
                            strategyName + "','" +
                            seqbuilder.toString() + "']");
                            //"<a href=\"/miso/library/" + l.getId() + "\"><span class=\"ui-icon ui-icon-pencil\"></span></a>" + "']");
            }
          }
        }
        j.put("elementsArray", jsonArray);
        return j;
      }
      catch (IOException e) {
        log.debug("Failed", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      }
    }
    else {
      return JSONUtils.SimpleJSONError("No plates to show");
    }
  }

  public JSONObject deletePlate(HttpSession session, JSONObject json) {
    User user;
    try {
      user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    catch (IOException e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Error getting currently logged in user.");
    }

    if (user != null && user.isAdmin()) {
      if (json.has("plateId")) {
        Long plateId = json.getLong("plateId");
        try {
          requestManager.deletePlate(requestManager.getPlateById(plateId));
          return JSONUtils.SimpleJSONResponse("Plate deleted");
        }
        catch (IOException e) {
          e.printStackTrace();
          return JSONUtils.SimpleJSONError("Cannot delete plate: " + e.getMessage());
        }
      }
      else {
        return JSONUtils.SimpleJSONError("No plate specified to delete.");
      }
    }
    else {
      return JSONUtils.SimpleJSONError("Only logged-in admins can delete objects.");
    }
  }


  public JSONObject searchSamples(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    try {
      List<Sample> samples;
      StringBuilder b = new StringBuilder();
      if (!"".equals(searchStr)) {
        samples = new ArrayList<Sample>(requestManager.listAllSamplesBySearch(searchStr));
      }
      else {
        samples = new ArrayList<Sample>(requestManager.listAllSamplesWithLimit(250));
      }

      if (samples.size() > 0) {
        Collections.sort(samples);
        Collections.reverse(samples);
        for (Sample s : samples) {
          b.append("<div id=\"sample"+s.getId()+"\" onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" "
                   + " " + "class=\"dashboard\">");
          b.append("<input type=\"hidden\" id=\"" + s.getId() + "\" name=\"" + s.getName() + "\" projectname=\"" + s.getProject().getName() + "\" samplealias=\"" + s.getAlias() + "\"/>");
          b.append("Name: <b>" + s.getName() + "</b><br/>");
          b.append("Alias: <b>" + s.getAlias() + "</b><br/>");
          b.append("From Project: <b>" + s.getProject().getName() + "</b><br/>");
          b.append("<button type=\"button\" class=\"fg-button ui-state-default ui-corner-all\" onclick=\"Plate.ui.insertSampleNextAvailable(jQuery('#sample"+s.getId()+"'));\">Add</button>");
          b.append("</div>");
        }
      }
      else {
        b.append("No matches");
      }
      return JSONUtils.JSONObjectResponse("html", b.toString());
    }
    catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONObject exportSampleForm(HttpSession session, JSONObject json) {
//    if (json.has("projectId") && json.has("documentFormat")) {
      try {
        JSONArray a = JSONArray.fromObject(json.getString("form"));
        File f = misoFileManager.getNewFile(
            Plate.class,
            "forms",
            "PlateInputForm-" + LimsUtils.getCurrentDateAsString() + ".xlsx");
        FormUtils.createPlateExportForm(f, a);
        return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
      }
      catch (Exception e) {
        e.printStackTrace();
        return JSONUtils.SimpleJSONError("Failed to get plate input form: " + e.getMessage());
      }
//    }
//    else {
//      return JSONUtils.SimpleJSONError("Missing project ID or document format supplied.");
//    }
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
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
}