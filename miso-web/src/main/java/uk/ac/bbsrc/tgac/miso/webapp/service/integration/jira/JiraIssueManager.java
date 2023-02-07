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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO. If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.miso.webapp.service.integration.jira;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import uk.ac.bbsrc.tgac.miso.core.data.Issue;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

import io.prometheus.client.Gauge;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.service.integration.jira
 * <p/>
 * Class to grab issues from a JIRA server via the JIRA REST API. Supports only HTTP basic authentication at present. OAuth is available but
 * untested and most likely not working.
 * 
 * @author Rob Davey
 * @date 20-Jan-2011
 * @since 0.0.3
 */
public class JiraIssueManager implements IssueTrackerManager {

  private static final String REST_API_URL = "/rest/api/";
  private static final String JIRA_REST_API_VERSION = "2";

  private static final Gauge errorGauge = Gauge.build().name("miso_jira_issue_manager_errors")
      .help("The number of consecutive failures to retrieve JIRA issues").register();

  private final DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  private String oAuthConsumerKey;
  private String oAuthConsumerSecret;
  private String oAuthSignatureMethod;

  private String httpBasicAuthUsername;
  private String httpBasicAuthPassword;

  private String baseTrackerUrl;

  public Client client;

  private URI getRestUri(String relativeUrl, Map<String, String> params) {
    StringBuilder sb = new StringBuilder();
    sb.append(baseTrackerUrl).append(REST_API_URL).append(JIRA_REST_API_VERSION);
    if (!relativeUrl.startsWith("/")) {
      sb.append("/");
    }
    sb.append(relativeUrl);
    if (params != null && !params.isEmpty()) {
      sb.append("?");
      params.forEach((key, value) -> sb.append(key).append("=").append(value).append("&"));
      sb.deleteCharAt(sb.lastIndexOf("&"));
    }
    return URI.create(sb.toString());
  }

  @Override
  public List<Issue> getIssuesByTag(String tag) throws IOException {
    Map<String, String> params = new HashMap<>();
    String replaceableTag = (tag == null ? "null" : tag); // project shortname might be null in plain sample mode
    params.put("jql", "labels=" + replaceableTag.replaceAll("\\s+", "-"));
    WebResource webResource = prepareWebResource(getRestUri("/search", params));
    return retrieveList(webResource);
  }

  @Override
  public List<Issue> searchIssues(String query) throws IOException {
    if (LimsUtils.isStringEmptyOrNull(query)) {
      return Collections.emptyList();
    }
    Map<String, String> params = new HashMap<>();
    params.put("jql", "text~'" + query.replaceAll("\\s+", "+") + "'");
    WebResource webResource = prepareWebResource(getRestUri("/search", params));
    return retrieveList(webResource);
  }

  private WebResource prepareWebResource(URI uri) {
    WebResource wr = null;
    if (httpBasicAuthUsername != null && httpBasicAuthPassword != null) {
      if (this.client == null) {
        DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        config.getState().setCredentials(null, null, -1, httpBasicAuthUsername, httpBasicAuthPassword);
        config.getProperties().put(ApacheHttpClientConfig.PROPERTY_PREEMPTIVE_AUTHENTICATION, true);
        ApacheHttpClientHandler ahcHandler = new ApacheHttpClientHandler(new HttpClient(new MultiThreadedHttpConnectionManager()), config);
        ApacheHttpClient ahc = new ApacheHttpClient(ahcHandler);
        this.client = ahc;
        wr = ahc.resource(uri);
      } else {
        wr = this.client.resource(uri);
      }
    } else if (oAuthConsumerKey != null && oAuthConsumerSecret != null && oAuthSignatureMethod != null) {
      if (this.client == null) {
        Client c = new Client();
        OAuthParameters params = new OAuthParameters().signatureMethod(oAuthSignatureMethod).consumerKey(oAuthConsumerKey).version("1.1");

        OAuthSecrets secrets = new OAuthSecrets().consumerSecret(oAuthConsumerSecret);
        OAuthClientFilter filter = new OAuthClientFilter(c.getProviders(), params, secrets);
        this.client = c;
        wr = c.resource(uri);
        wr.addFilter(filter);
      } else {
        wr = this.client.resource(uri);
      }
    } else {
      throw new IllegalStateException("No viable credentials to query for issue. Please check your IssueTrackerManager configuration.");
    }
    return wr;
  }

  private List<Issue> retrieveList(WebResource webResource) throws IOException {
    try {
      String json = webResource.get(String.class);
      JSONObject jsonData = JSONObject.fromObject(json);
      JSONArray jsonIssues = jsonData.getJSONArray("issues");
      List<Issue> issues = new ArrayList<>();
      for (int i = 0; i < jsonIssues.size(); i++) {
        issues.add(makeIssue(jsonIssues.getJSONObject(i)));
      }
      errorGauge.set(0);
      return issues;
    } catch (Exception e) {
      errorGauge.inc();
      throw new IOException("Unable to get resource", e);
    }
  }

  private Issue makeIssue(JSONObject json) {
    Issue issue = new Issue();
    String key = json.getString("key");
    issue.setKey(key);
    JSONObject fields = json.getJSONObject("fields");
    issue.setSummary(fields.getString("summary"));
    issue.setUrl(baseTrackerUrl + "/browse/" + key);
    JSONObject status = fields.getJSONObject("status");
    issue.setStatus(status.getString("name"));
    issue.setAssignee("(Unassigned)");
    if (fields.has("assignee")) {
      JSONObject assignee = fields.getJSONObject("assignee");
      if (!assignee.isNullObject() && assignee.has("displayName")) {
        issue.setAssignee(assignee.getString("displayName"));
      }
    }
    try {
      issue.setLastUpdated(iso8601Format.parse(fields.getString("updated")));
    } catch (ParseException e) {
      throw new IllegalArgumentException("Invalid date format: " + fields.getString("updated"), e);
    }
    return issue;
  }

  private abstract class ConfigValue {
    private final String name;

    protected ConfigValue(String name) {
      this.name = "miso.issuetracker.jira." + name;
    }

    protected abstract void set(String value);

    public void configure(Properties properties) {

      if (properties.containsKey(name)) {
        set(properties.getProperty(name));
      }
    }
  }

  @Override
  public void setConfiguration(Properties properties) {
    for (ConfigValue value : new ConfigValue[] {
        new ConfigValue("oAuthConsumerKey") {

          @Override
          protected void set(String value) {
            oAuthConsumerKey = value;
          }

        }, new ConfigValue("oAuthConsumerSecret") {

          @Override
          protected void set(String value) {
            oAuthConsumerSecret = value;
          }

        }, new ConfigValue("oAuthSignatureMethod") {

          @Override
          protected void set(String value) {
            oAuthSignatureMethod = value;
          }

        }, new ConfigValue("httpBasicAuthUsername") {

          @Override
          protected void set(String value) {
            httpBasicAuthUsername = value;
          }

        }, new ConfigValue("httpBasicAuthPassword") {

          @Override
          protected void set(String value) {
            httpBasicAuthPassword = value;
          }

        }, new ConfigValue("baseUrl") {

          @Override
          protected void set(String value) {
            baseTrackerUrl = value;
          }

        }

    }) {
      value.configure(properties);
    }
  }

}
