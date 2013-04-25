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

import com.sun.jersey.api.client.*;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.ApacheHttpClientHandler;
import com.sun.jersey.client.apache.config.ApacheHttpClientConfig;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.oauth.client.OAuthClientFilter;
import com.sun.jersey.oauth.signature.OAuthParameters;
import com.sun.jersey.oauth.signature.OAuthSecrets;
import net.sf.json.JSONObject;
import net.sourceforge.fluxion.spi.ServiceProvider;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import uk.ac.bbsrc.tgac.miso.core.manager.IssueTrackerManager;
import uk.ac.bbsrc.tgac.miso.core.util.jira.IssueJsonConverter;

import java.io.IOException;
import java.net.*;

/**
 * uk.ac.bbsrc.tgac.miso.webapp.service.integration.jira
 * <p/>
 * Class to grab issues from a JIRA server via the JIRA REST API.
 * Supports only HTTP basic authentication at present. OAuth is
 * available but untested and most likely not working.
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

  public final String jiraIssueSuffix = restApiUrl+jiraRestApiVersion+"/issue/";

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

  public String getBaseTrackerUrl() {
    return baseTrackerUrl;
  }

  public void setBaseTrackerUrl(String baseTrackerUrl) {
    this.baseTrackerUrl = baseTrackerUrl;
  }

  public void setClient(Client client) {
    this.client = client;
  }

  public String getType() {
    return IssueTrackerManager.TrackerType.JIRA.getKey();
  }

  public JSONObject getIssue(String issueKey) throws IOException {
    /*
    //DOESN'T WORK - THE KEYS ARE CORRECT AND ACCEPTED BY BOTH SERVER AND CLIENT, BUT AUTH STILL FAILS WITH A 401    
    Client client = new Client();
    OAuthParameters params = new OAuthParameters().signatureMethod("RSA-SHA1").
        consumerKey("n74697.nbi.ac.uk").version("1.1");

    OAuthSecrets secrets = new OAuthSecrets().consumerSecret(

            //
            //"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCfYzlFqYwWIP+b7N/hHcq+XjvH6js4Simd/VDBSFhtqVf7IG34AIkdGMm90o4E240+maHUOROsF21d6ZUAXWtr0wt8ddZ11QZTUvSF7e0w/wnwMqm9Jq+z6x9kF0lJKZN2j1MmUV85oPIOAqfSGbu5gzdU9bSIrnFeQbQI053mwIDAQAB"

            //PKCS8
            //"MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMm70m1lCP8SIs09MlfaXIRn9GJXKYjrH8npVJxRCJfxqM09UvDTKoJXFSmYxEztm+ohqFX86BjbTZIxJdLJy5u372pLj5lYDDu3ldNmF3c0npB5SlddQVzdmMe4Q1fmewTGuX8u4yuf1rxz60txuD8VHYmVdFL7Jv9eo+s1fsb9AgMBAAECgYBS08cGG2GiOLQJSIMPBxzNUFFKvzbSfJdEs4SF2c+JczM3cWJIdqsRcOfDeHHAlB5b9w3CoGA7NG+ZAozhCvGJdo/UyKOoQg6vdsJocAHOaJokhoC8y6e+DelFe5whS8pusz5eUZixdk3UpJdgciEzyo4iqNlih2M1QUesw+pbAQJBAPi/U4b9QNNKeQ/cLaD3O2k3ZtvcF8jF91ESv7VTTy5wWIK737HeO1aBSvDHTuf1QF101pYd1MdegZ6AZ1NTC/ECQQDPnZTD9WVddJtxtNw0YQ6s/cagT5UMlJK+RMWO2kuykHPjqW3dO7Wm/s0DT8dSc4RBvpHSlYTayuuwQr1GdKfNAkEA6Ba7/It3XK1z31R0TmffSwCiPClTF8V+SGrR7IFas92m9/QLd43+l2vzXEzaVpsB/zDwtwgGbC7FifoMbjZiQQJAfYQSPUothwuJMnwaKNyIp9Mg8yhJvvpPoRIosc2NuB2Wwota7UY88wHWZeskYNePnMOem0ZjtHa7eKtQjLjX/QJAcgnb3IeF4Nh041Waw3lroyZNtl+JBgU2lOWN4EFP1p6VTd0mfJzhhq87cdvWkpJugqNwoEO7WbueZb2zrN4MsQ=="
            "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMJ9jOUWpjBYg/5v" +
            "s3+Edyr5eO8fqOzhKKZ39UMFIWG2pV/sgbfgAiR0Yyb3SjgTbjT6ZodQ5E6wXbV3" +
            "plQBda2vTC3x11nXVBlNS9IXt7TD/CfAyqb0mr7PrH2QXSUkpk3aPUyZRXzmg8g4" +
            "Cp9IZu7mDN1T1tIiucV5BtAjTnebAgMBAAECgYApb+xz5rZzuHgu3oek1Ik7O7m7" +
            "YMhx56rshMRZih9JnUtwu4gLsAtkzvNNGwTSG7mwSeVMUTzDUyoz/pEOdt0mS4lE" +
            "bIHOL/qKSp/HTQ8JIKOvab6pZLl4OiR58lXjGIPzF+ZfOlgDOHVZE3dts4kYoW4B" +
            "nasylnmdjhA/bFAlEQJBAPeWhNtqwyssgBu2u3/YQ1JG1G3hLKknN0IOcSfhYW3i" +
            "LbuwHM34446nJbo3kn1f4YzaXOdYirJk/81WbbsT8pMCQQDJGTKLAceAFZbcuwXk" +
            "0K5DusnBNYHJTK4nWtVyHKhCovwFqdvm9gh1PbkCXwc9DSw2qZAbAFZCQO8cnN4w" +
            "bWPZAkB1wU8bFKISrK2ZgMWYvoD2Zt2uDQSyxwYPEtNaxmUSYBqkQ9TPWerQ9EqL" +
            "UNcJbkrWNR0uovwEcOkA/nReH9SlAkAfinMx+EJ5JWm3DyJahByBBP/17NWBZCSA" +
            "ia+mqTX+1Z9fqAeUjww+j0LAS3VQLwu7OxceFQxyccrPGw+CWvXpAkAkCQQM2Wkq" +
            "uEI4Zx25mx3nnH3Yav8mroWbBjCu1PWLJ1nhTBz8L2Jo6+CdlbfWswfJ17b0ov1+" +
            "7tl9en2jVS2P"
    );
    OAuthClientFilter filter = new OAuthClientFilter(client.getProviders(), params, secrets);

    WebResource webResource = client.resource(URI.create(jiraBaseUrl+issueUrlSuffix+issueKey));
    webResource.addFilter(filter);
    JSONObject jsonObject = webResource.get(JSONObject.class);

    //String responseMsg = webResource.path("oauth").get(String.class);
    //WebResource webResource = client.resource(URI.create(jiraBaseUrl+issueUrlSuffix+issueKey));
    //JSONObject jsonObject = webResource.get(JSONObject.class);
    System.out.println(jsonObject.toString());
    */

    /*
    final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory();
    final URI jiraServerUri;
    try {
      jiraServerUri = new URI(jiraBaseUrl);
      final JiraRestClient restClient = factory.createWithBasicHttpAutentication(jiraServerUri, "redmine", "redMine");
      */
      /*
      //DOESN'T WORK - SAME 401 AUTH PROBLEM AS ABOVE
      OAuthParameters params = new OAuthParameters();
      params.signatureMethod(RSA_SHA1.NAME);
      params.consumerKey("n74697.nbi.ac.uk");
      //params.setToken("2c9100f5a2394139840222786e9c519b");
      params.setVersion("1.0");
      params.nonce();

      OAuthSecrets secrets = new OAuthSecrets();
      secrets.consumerSecret("MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMm70m1lCP8SIs09MlfaXIRn9GJXKYjrH8npVJxRCJfxqM09UvDTKoJXFSmYxEztm+ohqFX86BjbTZIxJdLJy5u372pLj5lYDDu3ldNmF3c0npB5SlddQVzdmMe4Q1fmewTGuX8u4yuf1rxz60txuD8VHYmVdFL7Jv9eo+s1fsb9AgMBAAECgYBS08cGG2GiOLQJSIMPBxzNUFFKvzbSfJdEs4SF2c+JczM3cWJIdqsRcOfDeHHAlB5b9w3CoGA7NG+ZAozhCvGJdo/UyKOoQg6vdsJocAHOaJokhoC8y6e+DelFe5whS8pusz5eUZixdk3UpJdgciEzyo4iqNlih2M1QUesw+pbAQJBAPi/U4b9QNNKeQ/cLaD3O2k3ZtvcF8jF91ESv7VTTy5wWIK737HeO1aBSvDHTuf1QF101pYd1MdegZ6AZ1NTC/ECQQDPnZTD9WVddJtxtNw0YQ6s/cagT5UMlJK+RMWO2kuykHPjqW3dO7Wm/s0DT8dSc4RBvpHSlYTayuuwQr1GdKfNAkEA6Ba7/It3XK1z31R0TmffSwCiPClTF8V+SGrR7IFas92m9/QLd43+l2vzXEzaVpsB/zDwtwgGbC7FifoMbjZiQQJAfYQSPUothwuJMnwaKNyIp9Mg8yhJvvpPoRIosc2NuB2Wwota7UY88wHWZeskYNePnMOem0ZjtHa7eKtQjLjX/QJAcgnb3IeF4Nh041Waw3lroyZNtl+JBgU2lOWN4EFP1p6VTd0mfJzhhq87cdvWkpJugqNwoEO7WbueZb2zrN4MsQ==");

      AuthenticationHandler ah = new OAuthAuthenticationHandler(params, secrets);
      JiraRestClient restClient = factory.create(jiraServerUri, ah);
      */
      /*
      final NullProgressMonitor pm = new NullProgressMonitor();
      final Issue issue = restClient.getIssueClient().getIssue(issueKey, pm);

      System.out.println(issue);
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
    }
    */

    WebResource webResource = prepareWebResource(URI.create(baseTrackerUrl+jiraIssueSuffix+issueKey));
    if (webResource != null) {
      try {
        String json = webResource.get(String.class);
        if (json != null) {
          return IssueJsonConverter.jiraToMiso(JSONObject.fromObject(json));
        }
      }
      catch(Exception e) {
        throw new IOException("Unable to get resource: " + issueKey , e);
      }
      return null;
    }
    else {
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
      }
      else {
        wr = this.client.resource(uri);
      }
    }
    else if (oAuthConsumerKey != null && oAuthConsumerSecret != null && oAuthSignatureMethod != null) {
      if (this.client == null) {
        Client c = new Client();
        OAuthParameters params = new OAuthParameters()
          .signatureMethod(oAuthSignatureMethod)
          .consumerKey(oAuthConsumerKey)
          .version("1.1");

        OAuthSecrets secrets = new OAuthSecrets()
          .consumerSecret(oAuthConsumerSecret);
        OAuthClientFilter filter = new OAuthClientFilter(c.getProviders(), params, secrets);
        setClient(c);
        wr = c.resource(uri);
        wr.addFilter(filter);
      }
      else {
        wr = this.client.resource(uri);
      }
    }
    return wr;
  }
}