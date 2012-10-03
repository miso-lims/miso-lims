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

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;
import com.opensymphony.util.FileUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.PrintJob;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlateMaterialType;
import uk.ac.bbsrc.tgac.miso.core.exception.MisoPrintException;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.BarcodeFactory;
import uk.ac.bbsrc.tgac.miso.core.factory.barcode.MisoJscriptFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.MisoFilesManager;
import uk.ac.bbsrc.tgac.miso.core.manager.PrintManager;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.service.printing.MisoPrintService;
import uk.ac.bbsrc.tgac.miso.core.service.printing.context.PrintContext;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

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
    File temploc = new File(session.getServletContext().getRealPath("/")+"temp/");
    try {
      Plate plate = requestManager.getPlateById(plateId);
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
          return JSONUtils.SimpleJSONError("'"+json.getString("barcodeGenerator") + "' is not a valid barcode generator type");
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
      if (json.has("serviceName")) { serviceName = json.getString("serviceName"); }

      MisoPrintService<File, PrintContext<File>> mps = null;
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
          Plate plate = requestManager.getPlateById(plateId);
          //autosave the barcode if none has been previously generated
          if (plate.getIdentificationBarcode() == null || "".equals(plate.getIdentificationBarcode())) {
            requestManager.savePlate(plate);
          }
          File f = mps.getLabelFor(plate);
          if (f!=null) thingsToPrint.add(f);
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
      if (newLocation != null) {
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());
        Plate plate = requestManager.getPlateById(plateId);
        plate.setLocationBarcode(locationBarcode);
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

  public JSONObject getBarcodesForMaterialType(HttpSession session, JSONObject json) {
    Map<String, Object> responseMap = new HashMap<String, Object>();
    if (json.has("materialType") && !"".equals(json.getString("materialType"))) {
      String materialType = json.getString("materialType");
      StringBuilder srb = new StringBuilder();
      srb.append("<select name='tagBarcode' id='tagBarcodes'>");
      srb.append("<option value='0' selected='selected'>Please select...</option>");
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