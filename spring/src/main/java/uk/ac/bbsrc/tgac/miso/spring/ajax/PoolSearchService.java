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

package uk.ac.bbsrc.tgac.miso.spring.ajax;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.service.PoolService;

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
  private PoolService poolService;

  private abstract class PoolSearch {
    public abstract Collection<Pool> all(PlatformType type) throws IOException;

    public abstract Collection<Pool> search(PlatformType type, String query) throws IOException;
  }

  private class ReadyPools extends PoolSearch {

    @Override
    public Collection<Pool> all(PlatformType type) throws IOException {
      return poolService.listReadyPoolsByPlatform(type);
    }

    @Override
    public Collection<Pool> search(PlatformType type, String query) throws IOException {
      return poolService.listReadyPoolsByPlatformAndSearch(type, query);
    }
  }

  private class AllPools extends PoolSearch {

    @Override
    public Collection<Pool> all(PlatformType type) throws IOException {
      return poolService.listByPlatform(type);
    }

    @Override
    public Collection<Pool> search(PlatformType type, String query) throws IOException {
      return poolService.listByPlatformAndSearch(type, query);
    }

  }

  public JSONObject poolSearch(HttpSession session, JSONObject json) {
    String searchStr = json.getString("str");
    StringBuilder b = new StringBuilder();
    if (json.has("platformType")) {
      PlatformType platformType = PlatformType.valueOf(json.getString("platformType").toUpperCase());
      boolean readyOnly = json.getBoolean("readyOnly");
      try {
        Collection<Pool> pools;
        PoolSearch search = readyOnly ? new ReadyPools() : new AllPools();
        if (!isStringEmptyOrNull(searchStr)) {
          pools = search.search(platformType, searchStr);
          // Base64-encoded string, most likely a barcode image beeped in. decode and search
          if (pools.isEmpty()) {
            pools = search.search(platformType, new String(Base64.decodeBase64(searchStr)));
          }
        } else {
          pools = search.all(platformType);
        }
        if (pools.size() > 0) {
          List<Pool> rPools = new ArrayList<>(pools);
          Collections.reverse(rPools);
          for (Pool pool : rPools) {
            b.append(poolHtml(pool));
          }
        } else {
          b.append("No matches");
        }
        return JSONUtils.JSONObjectResponse("html", b.toString());
      } catch (IOException e) {
        log.debug("Failed", e);
        return JSONUtils.SimpleJSONError("Failed");
      }
    }
    return JSONUtils.JSONObjectResponse("html", "");
  }

  private String poolHtml(Pool p) {
    StringBuilder b = new StringBuilder();
    String lowquality = p.getHasLowQualityMembers() ? " lowquality" : "";
    b.append("<div style='position:relative' onMouseOver='this.className=\"dashboardhighlight" + lowquality
        + "\"' onMouseOut='this.className=\"dashboard" + lowquality + "\"' class='dashboard" + lowquality + "'>");
    if (p.getHasLowQualityMembers()) {
      b.append("<span class=\"lowquality-right\">Contains low-quality library</span>");
    }

    if (LimsUtils.isStringEmptyOrNull(p.getAlias())) {
      b.append("<div style=\"float:left\"><b>" + p.getName() + " : " + p.getCreationTime() + "</b><br/>");
    } else {
      b.append("<div style=\"float:left\"><b>" + p.getName() + " (" + p.getAlias() + ") : " + p.getCreationTime() + "</b><br/>");
    }

    List<PoolableElementView> ds = new ArrayList<>(p.getPoolableElementViews());
    for (int i = 0; i < Math.min(ds.size(), 5); i++) {
      PoolableElementView v = ds.get(i);
      b.append("<span" + (v.isLowQualityLibrary() ? " class='lowquality'" : "") + ">" + v.getDilutionName() + " ("
          + (v.getProjectShortName() == null ? v.getProjectAlias() : v.getProjectShortName()) + ") : " + v.getDilutionConcentration() + " "
          + PoolableElementView.getUnits() + "</span><br/>");
    }
    if (ds.size() > 5) {
      b.append("<span>...and " + (ds.size() - 5) + " more</span><br/>");
    }

    b.append("<br/><i>");
    Collection<Experiment> exprs = p.getExperiments();
    if (exprs != null) {
      for (Experiment e : exprs) {
        b.append(
            "<span>" + e.getStudy().getProject().getAlias() + "(" + e.getName() + ": " + p.getPoolableElementViews().size()
                + " dilutions)</span><br/>");
      }
    }
    b.append("</i>");

    b.append("<input type='hidden' id='pId" + p.getId() + "' value='" + p.getId() + "'/></div>");
    b.append("<div style='position: absolute; bottom: 0; right: 0; font-size: 24px; font-weight: bold; color:#BBBBBB'>"
        + p.getPlatformType().getKey() + "</div>");
    b.append("</div>");
    return b.toString();
  }
}
