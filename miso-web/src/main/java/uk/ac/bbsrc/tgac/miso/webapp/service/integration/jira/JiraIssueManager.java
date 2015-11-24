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

package uk.ac.bbsrc.tgac.miso.webapp.service.integration.jira;

import java.io.IOException;
import java.net.URI;

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

import net.sf.json.JSONObject;
import net.sourceforge.fluxion.spi.ServiceProvider;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.util.jira.IssueJsonConverter;

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
@ServiceProvider
public class JiraIssueManager implements IssueTrackerManager {
  private String oAuthConsumerKey;
  private String oAuthConsumerSecret;
  private String oAuthSignatureMethod;

  private String httpBasicAuthUsername;
  private String httpBasicAuthPassword;

  public String baseTrackerUrl;
  private final String restApiUrl = "/rest/api/";
  public String jiraRestApiVersion = "2";

  public final String jiraIssueSuffix = restApiUrl + jiraRestApiVersion + "/issue/";

  public Client client;

  public void setOAuthConsumerKey(String oAuthConsumerKey) {
    this.oAuthConsumerKey = oAuthConsumerKey;
  }

  public void setOAuthConsumerSecret(String oAuthConsumerSecret) {
    this.oAuthConsumerSecret = oAuthConsumerSecret;
  }

  public void setOAuthSignatureMethod(String oAuthSignatureMethod) {
    this.oAuthSignatureMethod = oAuthSignatureMethod;
  }

  public void setHttpBasicAuthUsername(String httpBasicAuthUsername) {
    this.httpBasicAuthUsername = httpBasicAuthUsername;
  }

  public void setHttpBasicAuthPassword(String httpBasicAuthPassword) {
    this.httpBasicAuthPassword = httpBasicAuthPassword;
  }

  @Override
  public String getBaseTrackerUrl() {
    return baseTrackerUrl;
  }

  public void setBaseTrackerUrl(String baseTrackerUrl) {
    this.baseTrackerUrl = baseTrackerUrl;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  @Override
  public String getType() {
    return IssueTrackerManager.TrackerType.JIRA.getKey();
  }

  @Override
  public JSONObject getIssue(String issueKey) throws IOException {
    WebResource webResource = prepareWebResource(URI.create(baseTrackerUrl + jiraIssueSuffix + issueKey));
    if (webResource != null) {
      try {
        String json = webResource.get(String.class);
        if (json != null) {
          return IssueJsonConverter.jiraToMiso(JSONObject.fromObject(json));
        }
      } catch (Exception e) {
        throw new IOException("Unable to get resource: " + issueKey, e);
      }
      return null;
    } else {
      throw new IOException("No viable resource to query for issue. Please check your IssueTrackerManager configuration.");
    }
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
        setClient(ahc);
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
        setClient(c);
        wr = c.resource(uri);
        wr.addFilter(filter);
      } else {
        wr = this.client.resource(uri);
      }
    }
    return wr;
  }
}