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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.StudyImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedDilutionException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolException;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedPoolQcException;
import uk.ac.bbsrc.tgac.miso.core.factory.DataObjectFactory;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;

/**
 * Created by IntelliJ IDEA. User: bianx Date: 18-Aug-2011 Time: 16:44:32 To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class PoolWizardControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(PoolControllerHelperService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;
  @Autowired
  private DataObjectFactory dataObjectFactory;

  public JSONObject addPool(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();

    DateFormat df = new SimpleDateFormat("dd/mm/yyyy");

    String alias = json.getString("alias");
    Double concentration = json.getDouble("concentration");
    PlatformType platformType = PlatformType.get(json.getString("platformType"));

    StringBuilder sb = new StringBuilder();
    List<Integer> ids = JSONArray.fromObject(json.getString("dilutions"));

    List<PoolQC> pqcs = new ArrayList<PoolQC>();
    JSONArray qcs = JSONArray.fromObject(json.get("qcs"));
    for (JSONObject q : (Iterable<JSONObject>) qcs) {
      PoolQC s = dataObjectFactory.getPoolQC();

      try {
        s.setResults(Double.valueOf(q.getString("poolQcResults")));
        s.setQcCreator(SecurityContextHolder.getContext().getAuthentication().getName());
        s.setQcDate(df.parse(q.getString("poolQcDate")));
        s.setQcType(requestManager.getPoolQcTypeById(q.getLong("poolQcType")));
      } catch (IOException e) {
        log.error("add pool", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      } catch (ParseException e) {
        log.error("add pool", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      }
      pqcs.add(s);
    }

    if (ids.size() > 0 && platformType != null && concentration != null) {
      try {
        User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

        List<Dilution> dils = new ArrayList<Dilution>();
        for (Integer id : ids) {
          dils.add(requestManager.getDilutionByIdAndPlatform(id.longValue(), platformType));
        }

        boolean barcodeCollision = false;
        if (dils.size() > 1) {
          for (Dilution d1 : dils) {
            if (d1 != null) {
              for (Dilution d2 : dils) {
                if (d2 != null && !d1.equals(d2)) {
                  if (!d1.getLibrary().getTagBarcodes().isEmpty() && !d2.getLibrary().getTagBarcodes().isEmpty()) {
                    if (d1.getLibrary().getTagBarcodes().equals(d2.getLibrary().getTagBarcodes())) {
                      barcodeCollision = true;
                    }
                  }
                }
              }
            }
          }
        }

        if (!barcodeCollision) {
          Pool pool;
          // TODO special type of pool for LibraryDilutions that will go on to be emPCRed as a whole
          if (dils.get(0) instanceof LibraryDilution
              && (platformType.equals(PlatformType.SOLID) || platformType.equals(PlatformType.LS454))) {
            pool = dataObjectFactory.getEmPCRPool(platformType, user);
          } else {
            pool = dataObjectFactory.getPoolOfType(platformType, user);
          }

          if (alias != null) {
            pool.setAlias(alias);
          }

          pool.setCreationDate(new Date());
          pool.setConcentration(concentration);
          pool.setPlatformType(platformType);
          pool.setReadyToRun(true);

          for (Dilution d : dils) {
            try {
              pool.addPoolableElement(d);
            } catch (MalformedDilutionException dle) {
              log.error("Failed", dle);
              return JSONUtils.SimpleJSONError("Failed: " + dle.getMessage());
            }
          }

          for (PoolQC qc : pqcs) {
            try {
              qc.setPool(pool);
              pool.addQc(qc);
            } catch (MalformedPoolException e) {
              log.error("Failed", e);
              return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
            } catch (MalformedPoolQcException e) {
              log.error("Failed", e);
              return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
            }
          }

          pool.setLastModifier(user);
          requestManager.savePool(pool);

          sb.append("<a class='dashboardresult' href='/miso/pool/" + pool.getId()
              + "' target='_blank'><div  onmouseover=\"this.className='dashboardhighlight ui-corner-all'\" onmouseout=\"this.className='dashboard ui-corner-all'\"  class='dashboard ui-corner-all' >");
          sb.append("Pool ID: <b>" + pool.getId() + "</b><br/>");
          sb.append("Pool Name: <b>" + pool.getName() + "</b><br/>");
          sb.append("Platform Type: <b>" + pool.getPlatformType().name() + "</b><br/>");
          sb.append("Dilutions: <ul class='bullets'>");
          for (Dilution dl : (Collection<? extends Dilution>) pool.getDilutions()) {
            sb.append("<li>" + dl.getName() + " (<a href='/miso/library/" + dl.getLibrary().getId() + "'>" + dl.getLibrary().getAlias()
                + "</a>)</li>");
          }
          sb.append("</ul>");

          sb.append("QCs: <ul class='bullets'>");
          for (PoolQC qc : (Collection<PoolQC>) pool.getPoolQCs()) {
            sb.append("<li>").append(qc.getResults()).append(" ").append(qc.getQcType().getUnits()).append(" (")
                .append(qc.getQcType().getName()).append(")</li>");
          }
          sb.append("</ul>");

          sb.append("</div></a>");
        } else {
          throw new IOException(
              "Tag barcode collision. Two or more selection dilutions have the same tag barcode and therefore cannot be pooled together.");
        }
      } catch (IOException e) {
        log.error("Failed", e);
        return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
      }
    } else {
      sb.append("<br/>No dilution available to save.");
    }

    response.put("html", sb.toString());
    return response;
  }

  public JSONObject addStudy(HttpSession session, JSONObject json) {
    String studyType = null;
    Long projectId = json.getLong("projectId");
    String studyDescription = null;

    StringBuilder sb = new StringBuilder();

    JSONArray a = JSONArray.fromObject(json.get("form"));
    for (JSONObject j : (Iterable<JSONObject>) a) {

      if (j.getString("name").equals("studyDescription")) {
        studyDescription = j.getString("value");
      } else if (j.getString("name").equals("studyType")) {
        studyType = j.getString("value");
      }
    }
    try {
      User user = securityManager.getUserByLoginName(SecurityContextHolder.getContext().getAuthentication().getName());

      Project p = requestManager.getProjectById(projectId);
      Study s = new StudyImpl();
      s.setProject(p);
      s.setAlias(p.getAlias());
      s.setDescription(studyDescription);
      s.setSecurityProfile(p.getSecurityProfile());
      s.setStudyType(studyType);

      s.setLastModifier(user);
      requestManager.saveStudy(s);

      sb.append("<a  class=\"dashboardresult\" href='/miso/study/" + s.getId()
          + "' target='_blank'><div onmouseover=\"this.className='dashboardhighlight ui-corner-all'\" onmouseout=\"this.className='dashboard ui-corner-all'\"  class='dashboard ui-corner-all' >New Study Added:<br/>");
      sb.append("Study ID: " + s.getId() + "<br/>");
      sb.append("Study Name: <b>" + s.getName() + "</b><br/>");
      sb.append("Study Alias: <b>" + s.getAlias() + "</b><br/>");
      sb.append("Study Description: <b>" + s.getDescription() + "</b></div></a><br/><hr/><br/>");
    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
    return JSONUtils.JSONObjectResponse("html", sb.toString());
  }

  public JSONObject populateDilutions(HttpSession session, JSONObject json) {
    Long projectId = json.getLong("projectId");
    PlatformType platformType = PlatformType.get(json.getString("platformType"));
    try {
      StringBuilder b = new StringBuilder();

      JSONArray a = new JSONArray();
      List<Dilution> dls = new ArrayList<Dilution>(requestManager.listAllDilutionsByProjectAndPlatform(projectId, platformType));
      Collections.sort(dls);
      for (Dilution dl : dls) {
        if (dl.getLibrary().getQcPassed() != null) {
          if (dl.getLibrary().getQcPassed()) {
            StringBuilder barcode = new StringBuilder();
            if (!dl.getLibrary().getTagBarcodes().isEmpty()) {
              int count = 0;
              for (TagBarcode tb : dl.getLibrary().getTagBarcodes().values()) {
                if (dl.getLibrary().getTagBarcodes().values().size() > 1 && count > 0) {
                  barcode.append("-");
                }
                barcode.append(tb.getName());
                count++;
              }
            }

            b.append("<tr id='" + dl.getId() + "'><td class='rowSelect'></td>");
            b.append("<td>" + dl.getName() + "</td>");
            b.append("<td>");
            b.append(barcode);
            b.append("</td>");
            b.append("</tr>");

            a.add(JSONObject
                .fromObject("{'id':" + dl.getId() + ",'name':'" + dl.getName() + "','description':'" + dl.getLibrary().getDescription()
                    + "','library':'" + dl.getLibrary().getAlias() + "','libraryBarcode':'" + barcode.toString() + "'}"));
          }
        }
      }

      JSONObject j = new JSONObject();
      j.put("dilutions", a);
      return JSONUtils.JSONObjectResponse(j);

    } catch (IOException e) {
      log.debug("Failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public void setSecurityManager(com.eaglegenomics.simlims.core.manager.SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(uk.ac.bbsrc.tgac.miso.core.manager.RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  public void setDataObjectFactory(DataObjectFactory dataObjectFactory) {
    this.dataObjectFactory = dataObjectFactory;
  }
}
