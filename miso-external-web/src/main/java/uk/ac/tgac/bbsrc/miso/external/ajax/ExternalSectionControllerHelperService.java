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

package uk.ac.tgac.bbsrc.miso.external.ajax;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.ajax.Ajaxified;
import net.sourceforge.fluxion.ajax.util.JSONUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

/**
 * Created by IntelliJ IDEA. User: bianx To change this template use File | Settings | File Templates.
 */
@Ajaxified
public class ExternalSectionControllerHelperService {
  protected static final Logger log = LoggerFactory.getLogger(ExternalSectionControllerHelperService.class);

  public JSONObject loginDisplayProjects(HttpSession session, JSONObject json) {
    JSONObject response = new JSONObject();
    JSONArray jarray = new JSONArray();

    String username = json.getString("username");
    String shapassword = json.getString("shapassword");
    String apiKey = null;
    StringBuilder b = new StringBuilder();

    try {

      apiKey = generatePrivateUserKey((username + "::" + shapassword).getBytes("UTF-8"));
      String signature = calculateHMAC("/miso/rest/external/projects?x-url=/miso/rest/external/projects@x-user=" + username, apiKey);
      HttpClient client = new DefaultHttpClient();
      String getURL = System.getProperty("miso.rooturl") + "/miso/rest/external/projects";
      HttpGet get = new HttpGet(getURL);
      get.setHeader("x-user", username);
      get.setHeader("x-signature", signature);
      get.setHeader("x-url", "/miso/rest/external/projects");
      HttpResponse responseGet = client.execute(get);
      HttpEntity resEntityGet = responseGet.getEntity();
      if (resEntityGet != null) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(resEntityGet.getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
          jarray = JSONArray.fromObject(line);
        }

      }
      // request done

      for (JSONObject jproject : (Iterable<JSONObject>) jarray) {

        b.append("<a class=\"dashboardresult\" onclick=\"showProjectStatus('"
            + jproject.getString("id")
            + "');\" href=\"javascript:void(0);\"><div  onMouseOver=\"this.className=&#39dashboardhighlight&#39\" onMouseOut=\"this.className=&#39dashboard&#39\" class=\"dashboard\">");
        b.append("Name: <b>" + jproject.getString("name") + "</b><br/>");
        b.append("Alias: <b>" + jproject.getString("alias") + "</b><br/>");
        b.append("</div></a>");
      }

      response.put("html", b.toString());
      return response;
    } catch (Exception e) {
      log.error("login display projects", e);
      return JSONUtils.SimpleJSONError("Failed: Problem with Login.");
    }
  }

  public JSONObject projectStatus(HttpSession session, JSONObject json) {
    JSONObject jsonObject = new JSONObject();
    try {
      String projectId = json.getString("projectId");
      JSONObject projectJSON = null;
      StringBuilder projectSb = new StringBuilder();
      StringBuilder sampleQcSb = new StringBuilder();
      JSONArray sampleArray = new JSONArray();
      JSONArray runsArray = new JSONArray();

      String username = json.getString("username");
      String shapassword = json.getString("shapassword");
      String apiKey = null;

      // request step 2

      apiKey = generatePrivateUserKey((username + "::" + shapassword).getBytes("UTF-8"));
      String signature = calculateHMAC("/miso/rest/external/project/" + projectId + "?x-url=/miso/rest/external/project/" + projectId
          + "@x-user=" + username, apiKey);
      HttpClient client = new DefaultHttpClient();
      String getURL = System.getProperty("miso.rooturl") + "/miso/rest/external/project/" + projectId;
      HttpGet get = new HttpGet(getURL);
      get.setHeader("x-user", username);
      get.setHeader("x-signature", signature);
      get.setHeader("x-url", "/miso/rest/external/project/" + projectId);
      HttpResponse responseGet = client.execute(get);
      HttpEntity resEntityGet = responseGet.getEntity();
      if (resEntityGet != null) {
        BufferedReader rd = new BufferedReader(new InputStreamReader(resEntityGet.getContent()));
        String line = "";
        while ((line = rd.readLine()) != null) {
          projectJSON = JSONObject.fromObject(line);
        }

      }

      if (projectJSON.getString("id").equals(projectId)) {
        JSONObject j = projectJSON;
        projectSb.append("<div class='report'>");
        projectSb.append("<b>Project Name: </b> " + j.getString("name"));
        projectSb.append("<br/><br/>");
        projectSb.append("<b>Project Alias: </b> " + j.getString("alias"));
        projectSb.append("<br/><br/>");
        projectSb.append("<b>Project Description: </b> " + j.getString("description"));
        projectSb.append("<br/><br/>");
        projectSb.append("<b>Progress: </b> " + j.getString("progress"));
        projectSb.append("<br/><br/>");

        if (j.getJSONArray("overviews").size() > 0) {
          for (JSONObject joverview : (Iterable<JSONObject>) j.getJSONArray("overviews")) {
            projectSb.append("<div><ol id=\"progress\">\n" + "            <li class=\"sample-qc-step\">\n");
            projectSb.append("<div class=\"");
            if (joverview.getBoolean("allSampleQcPassed") && joverview.getBoolean("libraryPreparationComplete")) {
              projectSb.append("left mid-progress-done");
            } else if (joverview.getBoolean("allSampleQcPassed")) {
              projectSb.append("left-progress-done");
            } else {
              projectSb.append("left");
            }
            projectSb.append("\">\n");
            projectSb.append("                <span>Sample QCs</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
                + "            <li class=\"lib-prep-step\">\n");
            projectSb.append("<div class=\"");
            if (joverview.getBoolean("libraryPreparationComplete") && joverview.getBoolean("allLibrariesQcPassed")) {
              projectSb.append("mid-progress-done");
            } else if (joverview.getBoolean("libraryPreparationComplete")) {
              projectSb.append("left-progress-done");
            } else {
              projectSb.append("");
            }
            projectSb.append("\">\n");
            projectSb.append("                <span>Libraries prepared</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
                + "            <li class=\"lib-qc-step\">\n");
            projectSb.append("<div class=\"");
            if (joverview.getBoolean("allLibrariesQcPassed") && joverview.getBoolean("allPoolsConstructed")) {
              projectSb.append("mid-progress-done");
            } else if (joverview.getBoolean("allLibrariesQcPassed")) {
              projectSb.append("left-progress-done");
            } else {
              projectSb.append("");
            }
            projectSb.append("\">\n");
            projectSb.append("                <span>Library QCs</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
                + "            <li class=\"pools-step\">\n");
            projectSb.append("<div class=\"");
            if (joverview.getBoolean("allPoolsConstructed") && joverview.getBoolean("allRunsCompleted")) {
              projectSb.append("mid-progress-done");
            } else if (joverview.getBoolean("allPoolsConstructed")) {
              projectSb.append("left-progress-done");
            } else {
              projectSb.append("");
            }
            projectSb.append("\">\n");
            projectSb.append("                <span>Pools Constructed</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
                + "            <li class=\"runs-step\">\n");
            projectSb.append("<div class=\"");
            if (joverview.getBoolean("allRunsCompleted") && joverview.getBoolean("primaryAnalysisCompleted")) {
              projectSb.append("mid-progress-done");
            } else if (joverview.getBoolean("allRunsCompleted")) {
              projectSb.append("left-progress-done");
            } else {
              projectSb.append("");
            }
            projectSb.append("\">\n");
            projectSb.append("                <span>Runs Completed</span>\n" + "              </div>\n" + "            </li>\n" + "\n"
                + "            <li class=\"primary-analysis-step\">\n");
            projectSb.append("<div class=\"");
            if (joverview.getBoolean("primaryAnalysisCompleted")) {
              projectSb.append("right mid-progress-done");
            } else {
              projectSb.append("right");
            }
            projectSb.append("\">\n");
            projectSb.append("                <span>Primary Analysis</span>\n" + "              </div>\n" + "            </li>\n"
                + "          </ol></div>\n" + "          <p style=\"clear:both\"/>");
          }
        }

        if (j.getJSONArray("samples").size() > 0) {
          int sampleQCPassed = 0;
          for (JSONObject jsample : (Iterable<JSONObject>) j.getJSONArray("samples")) {
            if ("true".equals(jsample.getString("qcPassed"))) {
              sampleQCPassed++;
            }

          }
          sampleQcSb.append("Sample QC Passed: " + sampleQCPassed + " out of " + j.getJSONArray("samples").size() + ".<br/><br/>");
          sampleArray = listSamplesDataTable(j.getJSONArray("samples"));
        }
        if (j.getJSONArray("runs").size() > 0) {
          runsArray = listRunsDataTable(j.getJSONArray("runs"));
        }
      }

      jsonObject.put("projectJson", projectSb.toString());
      jsonObject.put("sampleQcJson", sampleQcSb.toString());
      jsonObject.put("samplesArray", sampleArray);
      jsonObject.put("runsArray", runsArray);

      return jsonObject;
    } catch (Exception e) {
      log.error("failed", e);
      return JSONUtils.SimpleJSONError("Failed: " + e.getMessage());
    }
  }

  public JSONArray listSamplesDataTable(JSONArray array) {
    try {
      JSONArray jsonArray = new JSONArray();
      for (JSONObject jsample : (Iterable<JSONObject>) array) {

        jsonArray.add("['" + jsample.getString("alias") + "','" + jsample.getString("sampleType") + "','" + jsample.getString("qcPassed")
            + "','" + jsample.getString("sampleQubit") + " ng/&#956;l" + "','" + jsample.getString("receivedDate") + "']");
      }
      return jsonArray;
    } catch (Exception e) {
      JSONArray jsonArray = new JSONArray();
      return jsonArray;
    }
  }

  public JSONArray listRunsDataTable(JSONArray array) {
    try {
      JSONArray jsonArray = new JSONArray();
      for (JSONObject jrun : (Iterable<JSONObject>) array) {
        StringBuilder runsamples = new StringBuilder();
        if (jrun.getJSONArray("samples").size() > 0) {
          runsamples.append("<div class=\"samplelist\">" + jrun.getJSONArray("samples").size() + " Samples:");
          runsamples.append("<ul>");

          for (JSONObject jrunsample : (Iterable<JSONObject>) jrun.getJSONArray("samples")) {
            runsamples.append("<li>" + jrunsample.getString("sampleAlias") + "</li>");
          }
        }
        runsamples.append("</ul></div>");
        jsonArray.add("['" + jrun.getString("name") + "','" + jrun.getString("status") + "','" + jrun.getString("startDate") + "','"
            + jrun.getString("completionDate") + "','" + jrun.getString("platformType") + "','" + runsamples.toString() + "']");

      }
      return jsonArray;
    } catch (Exception e) {
      JSONArray jsonArray = new JSONArray();
      return jsonArray;
    }
  }

  public static String generatePrivateUserKey(byte[] data) throws NoSuchAlgorithmException {
    SecretKeySpec signingKey = new SecretKeySpec(data, "DSA");
    return Base64.encodeBase64URLSafeString(signingKey.getEncoded());
  }

  public static String calculateHMAC(String data, String key) throws java.security.SignatureException {
    String result;
    try {
      // get an hmac_sha1 key from the raw key bytes
      SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");

      // get an hmac_sha1 Mac instance and initialize with the signing key
      Mac mac = Mac.getInstance("HmacSHA1");
      mac.init(signingKey);

      // compute the hmac on input data bytes
      byte[] rawHmac = mac.doFinal(data.getBytes());

      // base64-encode the hmac
      result = Base64.encodeBase64URLSafeString(rawHmac);
    } catch (Exception e) {
      log.error("failed to generate HMAC", e);
      throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
    }
    return result;
  }

}
