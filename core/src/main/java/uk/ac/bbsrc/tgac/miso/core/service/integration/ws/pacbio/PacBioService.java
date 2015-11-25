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

package uk.ac.bbsrc.tgac.miso.core.service.integration.ws.pacbio;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PacBioService {
  protected static final Logger log = LoggerFactory.getLogger(PacBioService.class);
  private final HttpClient httpclient = new DefaultHttpClient();
  private final URI baseRestUri;
  private final DateFormat startDateFormat = new SimpleDateFormat("yyyy-MM-dd");

  public PacBioService(URI restLocation) {
    baseRestUri = restLocation;
  }

  public String getPrimaryAnalysisJob(String plateId, String sampleWellId, Date fromDate) {
    try {
      String d = startDateFormat.format(fromDate);
      HttpGet httpget = new HttpGet(baseRestUri.toString() + "Jobs/PrimaryAnalysis/Query?after=" + d);
      HttpResponse response = httpclient.execute(httpget);
      String out = parseEntity(response.getEntity());
      JSONArray a = JSONArray.fromObject(out);
      for (JSONObject j : (Iterable<JSONObject>) a) {
        if (j.getString("Plate").equals(plateId) && j.getString("Well").equals(sampleWellId)) {
          return j.toString();
        }
      }
    } catch (ClientProtocolException e) {
      log.error("get primary analyis job", e);
    } catch (IOException e) {
      log.error("get primary analyis job", e);
    }
    return null;
  }

  public String getPrimaryAnalysisStatus(String plateId, String sampleWellId) {
    HttpGet httpget = new HttpGet(baseRestUri.toString() + "/Jobs/PrimaryAnalysis/" + plateId + "/" + sampleWellId + "/Status");
    try {
      HttpResponse response = httpclient.execute(httpget);
      String out = parseEntity(response.getEntity());
      JSONObject j = JSONObject.fromObject(out);
      if (j.has("Status")) {
        return j.getString("Status");
      }
    } catch (ClientProtocolException e) {
      log.error("get primary analyis status", e);
    } catch (IOException e) {
      log.error("get primary analyis status", e);
    }
    return null;
  }

  public String getPlateStatus(String plateId) {
    HttpGet httpget = new HttpGet(baseRestUri.toString() + "/Jobs/Plate/" + plateId + "/Status");
    try {
      HttpResponse response = httpclient.execute(httpget);
      String out = parseEntity(response.getEntity());
      JSONObject j = JSONObject.fromObject(out);
      if (j.has("Status")) {
        return j.getString("Status");
      }
    } catch (ClientProtocolException e) {
      log.error("get plate status", e);
    } catch (IOException e) {
      log.error("get plate status", e);
    }
    return null;
  }

  private String parseEntity(HttpEntity entity) throws IOException {
    if (entity != null) {
      return EntityUtils.toString(entity, "UTF-8");
    } else {
      throw new IOException("Null entity in REST response");
    }
  }
}
