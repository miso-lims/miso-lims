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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.BarcodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.ac.bbsrc.tgac.miso.core.data.*;
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
public class ImportExportControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ImportExportControllerHelperService.class);
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private MisoFilesManager misoFileManager;

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
          String dnaOrRNA = "O";
          if ("GENOMIC".equals(s.getSampleType())
              || "METAGENOMIC".equals(s.getSampleType())) {
            dnaOrRNA = "D";
          }
          else if ("NON GENOMIC".equals(s.getSampleType())
                   || "VIRAL RNA".equals(s.getSampleType())
                   || "TRANSCRIPTOMIC".equals(s.getSampleType())
                   || "METATRANSCRIPTOMIC".equals(s.getSampleType())) {
            dnaOrRNA = "R";
          }
          b.append("<div id=\"sample" + s.getId() + "\" onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" "
                   + " " + "class=\"dashboard\">");
          b.append("<input type=\"hidden\" id=\"" + s.getId() + "\" name=\"" + s.getName() + "\" projectname=\"" + s.getProject().getName() + "\" projectalias=\"" + s.getProject().getAlias() + "\" samplealias=\"" + s.getAlias() + "\" dnaOrRNA=\"" + dnaOrRNA + "\"/>");
          b.append("Name: <b>" + s.getName() + "</b><br/>");
          b.append("Alias: <b>" + s.getAlias() + "</b><br/>");
          b.append("From Project: <b>" + s.getProject().getName() + "</b><br/>");
          b.append("<button type=\"button\" class=\"fg-button ui-state-default ui-corner-all\" onclick=\"ImportExport.insertSampleNextAvailable(jQuery('#sample" + s.getId() + "'));\">Add</button>");
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
    try {
      JSONArray a = JSONArray.fromObject(json.getString("form"));
      File f = misoFileManager.getNewFile(
          Sample.class,
          "forms",
          "SampleExportForm-" + LimsUtils.getCurrentDateAsString() + ".xlsx");
      FormUtils.createSampleExportForm(f, a);
      return JSONUtils.SimpleJSONResponse("" + f.getName().hashCode());
    }
    catch (Exception e) {
      e.printStackTrace();
      return JSONUtils.SimpleJSONError("Failed to get plate input form: " + e.getMessage());
    }
  }


  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setMisoFileManager(MisoFilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

}