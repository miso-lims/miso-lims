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

package uk.ac.bbsrc.tgac.miso.core.manager;

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import net.sf.json.JSONArray;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.Submittable;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.factory.submission.ERASubmissionFactory;
import uk.ac.bbsrc.tgac.miso.core.service.submission.ERAEndpoint;
import uk.ac.bbsrc.tgac.miso.core.service.submission.EndPoint;
import uk.ac.bbsrc.tgac.miso.core.service.submission.FTPTransferMethod;
import uk.ac.bbsrc.tgac.miso.core.service.submission.FilePathGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.submission.TGACIlluminaFilepathGenerator;
import uk.ac.bbsrc.tgac.miso.core.service.submission.TransferMethod;
import uk.ac.bbsrc.tgac.miso.core.service.submission.UploadReport;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;

/**
 * Manager class that holds state for a submission connection to the EBI SRA submission service, and facilitates the submission process
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public class ERASubmissionManager implements SubmissionManager<Set<Submittable<Document>>, URL, Document> {
  @Autowired
  private Properties submissionProperties;

  public void setSubmissionProperties(Properties submissionProperties) {
    this.submissionProperties = submissionProperties;
  }

  @Autowired
  private RequestManager requestManager;

  public void setRequestManager(RequestManager requestManager) {
    this.requestManager = requestManager;
  }

  @Autowired
  private FilesManager misoFileManager;

  public void setMisoFileManager(FilesManager misoFileManager) {
    this.misoFileManager = misoFileManager;
  }

  /** Field log */
  protected static final Logger log = LoggerFactory.getLogger(ERASubmissionManager.class);

  private String centreName;
  private String accountName;
  private String dropBox;
  private String authKey;
  private URL submissionEndPoint;
  private String submissionStoragePath;
  private Map<Long, UploadReport> uploadReports = new HashMap<Long, UploadReport>();

  private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  /**
   * Sets the centreName of this ERASubmissionManager object.
   * 
   * @param centreName
   *          centreName.
   */
  public void setCentreName(String centreName) {
    this.centreName = centreName;
  }

  /**
   * Sets the accountName of this ERASubmissionManager object.
   * 
   * @param accountName
   *          accountName.
   */
  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

  /**
   * Sets the dropBox of this ERASubmissionManager object.
   * 
   * @param dropBox
   *          dropBox.
   */
  public void setDropBox(String dropBox) {
    this.dropBox = dropBox;
  }

  /**
   * Sets the authKey of this ERASubmissionManager object.
   * 
   * @param authKey
   *          authKey.
   */
  public void setAuthKey(String authKey) {
    this.authKey = authKey;
  }

  /**
   * Sets the submissionEndPoint of this ERASubmissionManager object.
   * 
   * @param submissionEndPoint
   *          submissionEndPoint.
   */
  @Override
  public void setSubmissionEndPoint(URL submissionEndPoint) {
    this.submissionEndPoint = submissionEndPoint;
  }

  /**
   * Returns the submissionEndPoint of this ERASubmissionManager object.
   * 
   * @return URL submissionEndPoint.
   */
  @Override
  public URL getSubmissionEndPoint() {
    return this.submissionEndPoint;
  }

  /**
   * Sets the submissionStoragePath of this ERASubmissionManager object.
   * 
   * @param submissionStoragePath
   *          submissionStoragePath.
   */
  @Override
  public void setSubmissionStoragePath(String submissionStoragePath) {
    this.submissionStoragePath = submissionStoragePath;
  }

  /**
   * Returns the submissionStoragePath of this ERASubmissionManager object.
   * 
   * @return String submissionStoragePath.
   */
  @Override
  public String getSubmissionStoragePath() {
    return submissionStoragePath;
  }

  @Override
  public String generateSubmissionMetadata(Submission submission) throws SubmissionException {
    File subPath = new File(misoFileManager.getFileStorageDirectory() + "/submission/" + submission.getName());
    StringBuilder sb = new StringBuilder();
    try {
      DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

      String d = df.format(new Date());
      submissionProperties.put("submissionDate", d);

      if (LimsUtils.checkDirectory(subPath, true)) {
        Map<String, List<Submittable<Document>>> map = new HashMap<String, List<Submittable<Document>>>();
        map.put("study", new ArrayList<Submittable<Document>>());
        map.put("sample", new ArrayList<Submittable<Document>>());
        map.put("experiment", new ArrayList<Submittable<Document>>());
        map.put("run", new ArrayList<Submittable<Document>>());

        Set<Submittable<Document>> subs = submission.getSubmissionElements();
        for (Submittable<Document> sub : subs) {

          if (sub instanceof Study) {
            map.get("study").add(sub);
          } else if (sub instanceof Sample) {
            map.get("sample").add(sub);
          } else if (sub instanceof Experiment) {
            map.get("experiment").add(sub);
          } else if (sub instanceof SequencerPoolPartition) {
            map.get("run").add(sub);
          }
        }

        for (String key : map.keySet()) {
          List<Submittable<Document>> submittables = map.get(key);
          Document submissionDocument = docBuilder.newDocument();
          ERASubmissionFactory.generateSubmissionXML(submissionDocument, submittables, key, submissionProperties);

          // generate xml files on disk
          File f = new File(subPath, File.separator + submission.getName() + "_" + key + "_" + d + ".xml");
          if (f.exists()) {
            f.delete();
          }
          SubmissionUtils.transform(submissionDocument, f);

          sb.append(SubmissionUtils.transform(submissionDocument, true));
        }

        Document submissionDocument = docBuilder.newDocument();
        ERASubmissionFactory.generateParentSubmissionXML(submissionDocument, submission, submissionProperties);
        File f = new File(subPath, File.separator + submission.getName() + "_submission_" + d + ".xml");
        if (f.exists()) {
          f.delete();
        }
        SubmissionUtils.transform(submissionDocument, f, true);

        sb.append(SubmissionUtils.transform(submissionDocument, true));
      }
    } catch (ParserConfigurationException e) {
      log.error("generate submission metadata", e);
      throw new SubmissionException(e.getMessage());
    } catch (TransformerException e) {
      log.error("generate submission metadata", e);
      throw new SubmissionException(e.getMessage());
    } catch (IOException e) {
      log.error("generate submission metadata", e);
      throw new SubmissionException(
          "Cannot write to submission storage directory: " + subPath + ". Please check this directory exists and is writable.");
    } finally {
      submissionProperties.remove("submissionDate");
    }

    return sb.toString();
  }

  /**
   * Submits the given set of Submittables to the ERA submission service endpoint
   * 
   * @param submissionData
   *          of type Set<Submittable<Document>>
   * @return Document
   * @throws SubmissionException
   *           when an error occurred with the submission process
   */
  @Override
  public Document submit(Set<Submittable<Document>> submissionData) throws SubmissionException {
    try {

      DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

      if (submissionEndPoint == null) {
        throw new SubmissionException("No submission endpoint configured. Please check your submission.properties file.");
      }

      if (submissionData == null || submissionData.size() == 0) {
        throw new SubmissionException("No submission data set.");
      }

      if (accountName == null || dropBox == null || authKey == null) {
        throw new SubmissionException("An accountName, dropBox and authKey must be supplied!");
      }

      if (centreName == null) {
        throw new SubmissionException(
            "No centreName configured. Please check your submission.properties file and specify your Center Name as given by the SRA.");
      }

      String curl = "curl -k ";

      StringBuilder sb = new StringBuilder();
      sb.append(curl);
      String proxyHost = submissionProperties.getProperty("submission.proxyHost");
      String proxyUser = submissionProperties.getProperty("submission.proxyUser");
      String proxyPass = submissionProperties.getProperty("submission.proxyPass");

      if (!isStringEmptyOrNull(proxyHost)) {
        sb.append("-x ").append(proxyHost);

        if (!isStringEmptyOrNull(proxyUser)) {
          sb.append("-U ").append(proxyUser);

          if (!isStringEmptyOrNull(proxyPass)) {
            sb.append(":").append(proxyPass);
          }
        }
      }

      // submit via REST to endpoint
      try {
        Map<String, List<Submittable<Document>>> map = new HashMap<String, List<Submittable<Document>>>();
        map.put("study", new ArrayList<Submittable<Document>>());
        map.put("sample", new ArrayList<Submittable<Document>>());
        map.put("experiment", new ArrayList<Submittable<Document>>());
        map.put("run", new ArrayList<Submittable<Document>>());

        Document submissionXml = docBuilder.newDocument();
        String subName = null;

        String d = df.format(new Date());
        submissionProperties.put("submissionDate", d);

        for (Submittable<Document> s : submissionData) {
          if (s instanceof Submission) {
            ERASubmissionFactory.generateParentSubmissionXML(submissionXml, (Submission) s, submissionProperties);
            subName = ((Submission) s).getName();
          } else if (s instanceof Study) {
            map.get("study").add(s);
          } else if (s instanceof Sample) {
            map.get("sample").add(s);
          } else if (s instanceof Experiment) {
            map.get("experiment").add(s);
          } else if (s instanceof SequencerPoolPartition) {
            map.get("run").add(s);
          }
        }

        if (submissionXml != null && subName != null) {
          String url = getSubmissionEndPoint() + "?auth=ERA%20" + dropBox + "%20" + authKey;
          HttpClient httpclient = getEvilTrustingTrustManager(new DefaultHttpClient());
          HttpPost httppost = new HttpPost(url);
          MultipartEntity reqEntity = new MultipartEntity();

          String submissionXmlFileName = subName + File.separator + subName + "_submission_" + d + ".xml";

          File subtmp = new File(submissionStoragePath + submissionXmlFileName);
          SubmissionUtils.transform(submissionXml, subtmp, true);

          reqEntity.addPart("SUBMISSION", new FileBody(subtmp));
          for (String key : map.keySet()) {
            List<Submittable<Document>> submittables = map.get(key);
            String submittableXmlFileName = subName + File.separator + subName + "_" + key.toLowerCase() + "_" + d + ".xml";
            File elementTmp = new File(submissionStoragePath + submittableXmlFileName);
            Document submissionDocument = docBuilder.newDocument();
            ERASubmissionFactory.generateSubmissionXML(submissionDocument, submittables, key, submissionProperties);
            SubmissionUtils.transform(submissionDocument, elementTmp, true);
            reqEntity.addPart(key.toUpperCase(), new FileBody(elementTmp));
          }

          httppost.setEntity(reqEntity);
          HttpResponse response = httpclient.execute(httppost);

          if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity resEntity = response.getEntity();
            try {
              Document submissionReport = docBuilder.newDocument();
              SubmissionUtils.transform(resEntity.getContent(), submissionReport);
              File savedReport = new File(submissionStoragePath + subName + File.separator + "report_" + d + ".xml");
              SubmissionUtils.transform(submissionReport, savedReport);
              return submissionReport;
            } catch (IOException e) {
              log.error("submission report", e);
            } catch (TransformerException e) {
              log.error("submission report", e);
            } finally {
              submissionProperties.remove("submissionDate");
            }
          } else {
            throw new SubmissionException(
                "Response from submission endpoint (" + url + ") was not OK (200). Was: " + response.getStatusLine().getStatusCode());
          }
        } else {
          throw new SubmissionException("Could not find a Submission in the supplied set of Submittables");
        }
      } catch (IOException e) {
        log.error("submission report", e);
      } catch (TransformerException e) {
        log.error("submission report", e);
      } finally {
        submissionProperties.remove("submissionDate");
      }
    } catch (ParserConfigurationException e) {
      log.error("submission report", e);
    }
    return null;
  }

  @Override
  public Map<String, Object> parseResponse(Document report) {
    Map<String, Object> responseMap = new HashMap<String, Object>();
    NodeList errors = report.getElementsByTagName("ERROR");
    NodeList infos = report.getElementsByTagName("INFO");

    ArrayList<String> errorList = new ArrayList<String>();
    if (errors.getLength() > 0) {
      for (int i = 0; i < errors.getLength(); i++) {
        errorList.add(errors.item(i).getTextContent());
      }
      responseMap.put("errors", JSONArray.fromObject(errorList));
    }

    ArrayList<String> infoList = new ArrayList<String>();
    if (infos.getLength() > 0) {
      for (int i = 0; i < infos.getLength(); i++) {
        infoList.add(infos.item(i).getTextContent());
      }
      responseMap.put("infos", JSONArray.fromObject(infoList));
    }
    return responseMap;
  }

  @Override
  public String submitSequenceData(Submission s) {
    Set<File> dataFiles = new HashSet<File>();
    FilePathGenerator FPG = new TGACIlluminaFilepathGenerator();

    for (Object o : s.getSubmissionElements()) {
      if (o instanceof SequencerPoolPartition) {
        SequencerPoolPartition l = (SequencerPoolPartition) o;
        try {
          dataFiles = FPG.generateFilePaths(l);
        } catch (SubmissionException submissionException) {
          log.error("submit sequence data", submissionException);
        }
      }
    }

    if (dataFiles.size() > 0) {
      TransferMethod t = new FTPTransferMethod();
      EndPoint end = new ERAEndpoint();
      end.setDestination(URI.create("ftp://localhost"));

      try {
        UploadReport report = t.uploadSequenceData(dataFiles, end);
        uploadReports.put(s.getId(), report);

        return ("Attempting to upload files...");
      } catch (Exception e) {
        log.error("failed to upload", e);
        return ("There was an error: " + e.getMessage());
      }
    } else
      return ("No datafiles were found to upload");
  }

  @Override
  public UploadReport getUploadProgress(Long submissionId) {
    try {
      return uploadReports.get(submissionId);
    } catch (Exception e) {
      log.error("failed to get upload reports", e);
    }
    return null;
  }

  @Override
  public void setTransferMethod(TransferMethod transferMethod) {

  }

  /*
   * public FTPUploadReport getUploadReport(Submission submission){ return uploadReports.get(submission); }
   */

  @Override
  public String prettifySubmissionMetadata(Submission submission) throws SubmissionException {
    StringBuilder sb = new StringBuilder();
    try {
      Collection<File> files = misoFileManager.getFiles(Submission.class, submission.getName());

      Date latestDate = null;

      // get latest submitted xmls
      try {
        for (File f : files) {
          if (f.getName().contains("submission_")) {
            String d = f.getName().substring(f.getName().lastIndexOf("_") + 1, f.getName().lastIndexOf("."));
            Date test = df.parse(d);
            if (latestDate == null || test.after(latestDate)) {
              latestDate = test;
            }
          }
        }
      } catch (ParseException e) {
        log.error("No timestamped submission metadata documents. Falling back to simple names", e);
      }

      String dateStr = "";
      if (latestDate != null) {
        dateStr = "_" + df.format(latestDate);
      }

      InputStream in = null;
      for (File f : files) {
        if (f.getName().contains("submission" + dateStr)) {
          in = ERASubmissionManager.class.getResourceAsStream("/submission/xsl/eraSubmission.xsl");
          if (in != null) {
            String xsl = LimsUtils.inputStreamToString(in);
            sb.append(SubmissionUtils.xslTransform(SubmissionUtils.transform(f, true), xsl));
          }
        }
      }

      for (File f : files) {
        if (f.getName().contains("study" + dateStr)) {
          in = ERASubmissionManager.class.getResourceAsStream("/submission/xsl/eraStudy.xsl");
          if (in != null) {
            String xsl = LimsUtils.inputStreamToString(in);
            sb.append(SubmissionUtils.xslTransform(SubmissionUtils.transform(f, true), xsl));
          }
        }
      }

      for (File f : files) {
        if (f.getName().contains("sample" + dateStr)) {
          in = ERASubmissionManager.class.getResourceAsStream("/submission/xsl/eraSample.xsl");
          if (in != null) {
            String xsl = LimsUtils.inputStreamToString(in);
            sb.append(SubmissionUtils.xslTransform(SubmissionUtils.transform(f, true), xsl));
          }
        }
      }

      for (File f : files) {
        if (f.getName().contains("experiment" + dateStr)) {
          in = ERASubmissionManager.class.getResourceAsStream("/submission/xsl/eraExperiment.xsl");
          if (in != null) {
            String xsl = LimsUtils.inputStreamToString(in);
            sb.append(SubmissionUtils.xslTransform(SubmissionUtils.transform(f, true), xsl));
          }
        }
      }

      for (File f : files) {
        if (f.getName().contains("run" + dateStr)) {
          in = ERASubmissionManager.class.getResourceAsStream("/submission/xsl/eraRun.xsl");
          if (in != null) {
            String xsl = LimsUtils.inputStreamToString(in);
            sb.append(SubmissionUtils.xslTransform(SubmissionUtils.transform(f, true), xsl));
          }
        }
      }
    } catch (IOException e) {
      log.error("prettify submission data", e);
    } catch (TransformerException e) {
      log.error("prettify submission data", e);
    }

    return sb.toString();
  }

  /**
   * Builds a "trusting" trust manager. This is totally horrible and basically ignores everything that SSL stands for. This allows
   * connection to self-signed certificate hosts, bypassing the normal validation exceptions that occur.
   * <p/>
   * Use at your own risk - again, this is horrible!
   */
  public DefaultHttpClient getEvilTrustingTrustManager(DefaultHttpClient httpClient) {
    try {
      // First create a trust manager that won't care about any SSL self-cert problems - eurgh!
      X509TrustManager trustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
          log.warn("BYPASSING CLIENT TRUSTED CHECK!");
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
          log.warn("BYPASSING SERVER TRUSTED CHECK!");
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
          log.warn("BYPASSING CERTIFICATE ISSUER CHECKS!");
          return null;
        }
      };

      // Now put the trust manager into an SSLContext
      SSLContext sslcontext = SSLContext.getInstance("TLS");
      sslcontext.init(null, new TrustManager[] { trustManager }, null);
      SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
      sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

      // If you want a thread safe client, use the ThreadSafeConManager, but
      // otherwise just grab the one from the current client, and get hold of its
      // schema registry. THIS IS THE KEY THING.
      ClientConnectionManager ccm = httpClient.getConnectionManager();
      SchemeRegistry schemeRegistry = ccm.getSchemeRegistry();

      // Register our new socket factory with the typical SSL port and the
      // correct protocol name.
      schemeRegistry.register(new Scheme("https", sf, 443));

      // Finally, apply the ClientConnectionManager to the Http Client
      // or, as in this example, create a new one.
      return new DefaultHttpClient(ccm, httpClient.getParams());
    } catch (Throwable t) {
      log.error("Something nasty happened with the EvilTrustingTrustManager. Warranty is null and void!", t);
      return null;
    }
  }
}
