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

import com.eaglegenomics.simlims.core.manager.SecurityManager;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.manager.RequestManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

/**
 * uk.ac.bbsrc.tgac.miso.miso.spring.ajax
 * <p/>
 * Info
 *
 * @author Xingdong Bian
 * @author Rob Davey
 * @since 0.0.2
 */
@Ajaxified
public class PoolSearchService {

  protected static final Logger log = LoggerFactory.getLogger(PoolSearchService.class);
  @Autowired
  private SecurityManager securityManager;
  @Autowired
  private RequestManager requestManager;

  public JSONObject poolSearch(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    StringBuilder b = new StringBuilder();
    if (json.has("platformType")) {
      String platformType = json.getString("platformType").toUpperCase();
      boolean readyOnly = json.getBoolean("readyOnly");
      try {
        Collection<Pool<? extends Poolable>> pools = null;
        if (searchStr != null && !searchStr.equals("")) {
          if (LimsUtils.isBase64String(searchStr)) {
            //Base64-encoded string, most likely a barcode image beeped in. decode and search
            searchStr = new String(Base64.decodeBase64(searchStr));
          }

          if (readyOnly) {
            pools = requestManager.listReadyPoolsByPlatformAndSearch(PlatformType.valueOf(platformType), searchStr);
          }
          else {
            pools = requestManager.listAllPoolsByPlatformAndSearch(PlatformType.valueOf(platformType), searchStr);
          }
        }
        else {
          if (readyOnly) {
            pools = requestManager.listReadyPoolsByPlatform(PlatformType.valueOf(platformType));
          }
          else {
            pools = requestManager.listAllPoolsByPlatform(PlatformType.valueOf(platformType));
          }
        }
        if (pools.size() > 0) {
          for (Pool pool : pools) {
            b.append(poolHtml(pool));
          }
        }
        else {
          b.append("No matches");
        }
        return JSONUtils.JSONObjectResponse("html", b.toString());
      }
      catch (IOException e) {
        log.debug("Failed", e);
        return JSONUtils.SimpleJSONError("Failed");
      }
    }
    return JSONUtils.JSONObjectResponse("html", "");
  }

  private String poolHtml(Pool p) {
    StringBuilder b = new StringBuilder();
    b.append("<div style='position:relative' onMouseOver='this.className=\"dashboardhighlight\"' onMouseOut='this.className=\"dashboard\"' class='dashboard' ondblclick='Run.container.insertPoolNextAvailable(this);'>");
    b.append("<div style=\"float:left\"><b>" + p.getName() + " (" + p.getCreationDate() + ")</b><br/>");

    Collection<Dilution> ds = p.getDilutions();
    for (Dilution d : ds) {
      b.append("<span>" + d.getName() + " (" + d.getLibrary().getSample().getProject().getAlias() + ")</span><br/>");
    }

    b.append("<br/><i>");
    Collection<Experiment> exprs = p.getExperiments();
    for (Experiment e : exprs) {
      b.append("<span>" + e.getStudy().getProject().getAlias() + "(" + e.getName() + ": " + p.getDilutions().size() + " dilutions)</span><br/>");
    }
    b.append("</i>");

    b.append("<input type='hidden' id='pId" + p.getId() + "' value='" + p.getId() + "'/></div>");
    b.append("<div style='position: absolute; bottom: 0; right: 0; font-size: 24px; font-weight: bold; color:#BBBBBB'>" + p.getPlatformType().getKey() + "</div>");
    b.append("</div>");
    return b.toString();
  }

  public void setSecurityManager(SecurityManager securityManager) {
    this.securityManager = securityManager;
  }

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }
}
