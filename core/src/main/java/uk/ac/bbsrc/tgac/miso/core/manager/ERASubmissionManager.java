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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.sf.json.JSONArray;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolableElementView;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
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
public class ERASubmissionManager implements SubmissionManager {
  @Value("#{ beanFactory.getBean(T(uk.ac.bbsrc.tgac.miso.webapp.util.MisoPropertyExporter)).getPropertiesAsProperties() }")
  private Properties submissionProperties;

  public void setSubmissionProperties(Properties submissionProperties) {
    this.submissionProperties = submissionProperties;
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
  private final Map<Long, UploadReport> uploadReports = new HashMap<>();

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
  public void setSubmissionEndPoint(URL submissionEndPoint) {
    this.submissionEndPoint = submissionEndPoint;
  }

  /**
   * Returns the submissionEndPoint of this ERASubmissionManager object.
   * 
   * @return URL submissionEndPoint.
   */
  public URL getSubmissionEndPoint() {
    return this.submissionEndPoint;
  }

  /**
   * Sets the submissionStoragePath of this ERASubmissionManager object.
   * 
   * @param submissionStoragePath
   *          submissionStoragePath.
   */
  public void setSubmissionStoragePath(String submissionStoragePath) {
    this.submissionStoragePath = submissionStoragePath;
  }

  /**
   * Returns the submissionStoragePath of this ERASubmissionManager object.
   * 
   * @return String submissionStoragePath.
   */
  public String getSubmissionStoragePath() {
    return submissionStoragePath;
  }

  private final class ExperimentXmlSubfile extends XmlSubmissionFromSet<Experiment> {
    @Override
    protected Set<Experiment> items(Submission submission) {
      return submission.getExperiments();
    }

    @Override
    protected String xmlName() {
      return "EXPERIMENT";
    }

    @Override
    protected String xmlCollectionName() {
      return "EXPERIMENT_SET";
    }

    @Override
    protected void populate(Element xml, Experiment experiment) {
      xml.setAttribute("alias", experiment.getAlias());
      xml.setAttribute("center_name", submissionProperties.getProperty("submission.centreName"));

      Element xmlTitle = xml.getOwnerDocument().createElementNS(null, "TITLE");
      xmlTitle.setTextContent(experiment.getTitle());
      xml.appendChild(xmlTitle);

      Element xmlStudyRef = xml.getOwnerDocument().createElementNS(null, "STUDY_REF");
      xmlStudyRef.setAttribute("refname", experiment.getStudy().getAlias());
      xmlStudyRef.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
      if (!isStringEmptyOrNull(experiment.getStudy().getAccession())) {
        xmlStudyRef.setAttribute("accession", experiment.getStudy().getAccession());
      }
      xml.appendChild(xmlStudyRef);

      Element xmlDesign = xml.getOwnerDocument().createElementNS(null, "DESIGN");
      xml.appendChild(xmlDesign);

      Element designDescription = xml.getOwnerDocument().createElementNS(null, "DESIGN_DESCRIPTION");
      designDescription.setTextContent(experiment.getDescription());
      xmlDesign.appendChild(designDescription);

      Element sampleDescriptor = xml.getOwnerDocument().createElementNS(null, "SAMPLE_DESCRIPTOR");
      sampleDescriptor.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));

      PoolableElementView relevantLibrary = null;

      if (experiment.getPool() != null) {
        if (experiment.getPool().getPoolableElementViews().size() > 1) {
          // multiplexed pool
          Element xmlPool = xml.getOwnerDocument().createElementNS(null, "POOL");
          sampleDescriptor.appendChild(xmlPool);

          for (PoolableElementView dilution : experiment.getPool().getPoolableElementViews()) {
            relevantLibrary = dilution;
            Element xmlMember = xml.getOwnerDocument().createElementNS(null, "MEMBER");
            xmlMember.setAttribute("member_name", dilution.getDilutionName());
            xmlMember.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
            xmlMember.setAttribute("refname", dilution.getSampleAlias());
            if (!isStringEmptyOrNull(dilution.getSampleAccession())) {
              sampleDescriptor.setAttribute("accession", dilution.getSampleAccession());
            }
            xmlPool.appendChild(xmlMember);

            Element xmlReadLabel = xml.getOwnerDocument().createElementNS(null, "READ_LABEL");
            if (!dilution.getIndices().isEmpty()) {
              StringBuilder tsb = new StringBuilder();
              StringBuilder vsb = new StringBuilder();
              for (Index index : dilution.getIndices()) {
                tsb.append(index.getSequence());
                vsb.append(index.getName());
              }
              xmlReadLabel.setAttribute("read_group_tag", tsb.toString());
              xmlReadLabel.setTextContent(vsb.toString());
            }
            xmlMember.appendChild(xmlReadLabel);
          }
        } else {
          for (PoolableElementView dilution : experiment.getPool().getPoolableElementViews()) {
            relevantLibrary = dilution;
            sampleDescriptor.setAttribute("refname", dilution.getSampleAlias());
            sampleDescriptor.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
            if (!isStringEmptyOrNull(dilution.getSampleAccession())) {
              sampleDescriptor.setAttribute("accession", dilution.getSampleAccession());
            }
          }
        }
      }

      xmlDesign.appendChild(sampleDescriptor);

      Element libraryDescriptor = xml.getOwnerDocument().createElementNS(null, "LIBRARY_DESCRIPTOR");
      Element libraryName = xml.getOwnerDocument().createElementNS(null, "LIBRARY_NAME");
      if (relevantLibrary != null) {
        if (!isStringEmptyOrNull(experiment.getPool().getAlias())) {
          libraryName.setTextContent(experiment.getPool().getAlias());
        } else {
          libraryName.setTextContent(experiment.getPool().getName());
        }
      }
      libraryDescriptor.appendChild(libraryName);

      Element libraryStrategy = xml.getOwnerDocument().createElementNS(null, "LIBRARY_STRATEGY");
      if (relevantLibrary != null) {
        libraryStrategy.setTextContent(relevantLibrary.getLibraryStrategyType());
      }
      libraryDescriptor.appendChild(libraryStrategy);

      Element librarySource = xml.getOwnerDocument().createElementNS(null, "LIBRARY_SOURCE");
      if (relevantLibrary != null) {
        librarySource.setTextContent(relevantLibrary.getSampleType());
      }
      libraryDescriptor.appendChild(librarySource);

      Element librarySelection = xml.getOwnerDocument().createElementNS(null, "LIBRARY_SELECTION");
      if (relevantLibrary != null) {
        librarySelection.setTextContent(relevantLibrary.getLibrarySelectionType());
      }
      libraryDescriptor.appendChild(librarySelection);

      Element libraryLayout = xml.getOwnerDocument().createElementNS(null, "LIBRARY_LAYOUT");
      Element layout;
      if (relevantLibrary != null) {
        if (relevantLibrary.isLibraryPaired()) {
          layout = xml.getOwnerDocument().createElementNS(null, "PAIRED");
          if (relevantLibrary.getLibraryDnaSize() != null) {
            layout.setAttribute("NOMINAL_LENGTH", relevantLibrary.getLibraryDnaSize().toString());
          } else {
            layout.setAttribute("NOMINAL_LENGTH", "0");
          }
        } else {
          layout = xml.getOwnerDocument().createElementNS(null, "SINGLE");
        }
        libraryLayout.appendChild(layout);
      }
      libraryDescriptor.appendChild(libraryLayout);

      Element poolingStrategy = xml.getOwnerDocument().createElementNS(null, "POOLING_STRATEGY");
      if (experiment.getPool() != null) {
        if (experiment.getPool().getPoolableElementViews().size() > 1) {
          poolingStrategy.setTextContent("multiplexed libraries");
        } else {
          poolingStrategy.setTextContent("none");
        }
      }
      libraryDescriptor.appendChild(poolingStrategy);

      xmlDesign.appendChild(libraryDescriptor);
      if (experiment.getPlatform() != null && experiment.getPlatform().getPlatformType().getSraName() != null) {
        Element platform = xml.getOwnerDocument().createElementNS(null, "PLATFORM");
        Element type = xml.getOwnerDocument().createElementNS(null, experiment.getPlatform().getPlatformType().getSraName());
        platform.appendChild(type);

        Element model = xml.getOwnerDocument().createElementNS(null, "INSTRUMENT_MODEL");
        model.setTextContent(experiment.getPlatform().getInstrumentModel());
        type.appendChild(model);
        xml.appendChild(platform);
      }
      Element processing = xml.getOwnerDocument().createElementNS(null, "PROCESSING");
      xml.appendChild(processing);
    }

    @Override
    public String name() {
      return "experiment";
    }
  }

  private final class StudyXmlSubfile extends XmlSubmissionFromSet<Study> {
    @Override
    public String name() {
      return "study";
    }

    @Override
    public Set<Study> items(Submission submission) {
      return submission.getStudies();
    }

    @Override
    protected String xmlName() {
      return "STUDY";
    }

    @Override
    protected String xmlCollectionName() {
      return "STUDY_SET";
    }

    @Override
    protected void populate(Element xml, Study s) {
      xml.setAttribute("alias", s.getAlias());

      Element studyDescriptor = xml.getOwnerDocument().createElementNS(null, "DESCRIPTOR");
      xml.appendChild(studyDescriptor);

      Element studyTitle = xml.getOwnerDocument().createElementNS(null, "STUDY_TITLE");
      studyTitle.setTextContent(s.getAlias());
      studyDescriptor.appendChild(studyTitle);

      Element studyType = xml.getOwnerDocument().createElementNS(null, "STUDY_TYPE");
      studyType.setAttribute("existing_study_type", s.getStudyType().getName());
      studyDescriptor.appendChild(studyType);

      Element centerProjectName = xml.getOwnerDocument().createElementNS(null, "CENTER_PROJECT_NAME");
      centerProjectName.setTextContent(s.getProject().getAlias());
      studyDescriptor.appendChild(centerProjectName);

      Element studyAbstract = xml.getOwnerDocument().createElementNS(null, "STUDY_ABSTRACT");
      studyDescriptor.appendChild(studyAbstract);

      Element studyDescription = xml.getOwnerDocument().createElementNS(null, "STUDY_DESCRIPTION");
      studyDescription.setTextContent(s.getDescription());
      studyDescriptor.appendChild(studyDescription);
    }
  }

  private final class SampleXmlSubfile extends XmlSubmissionFromSet<Sample> {
    @Override
    protected Set<Sample> items(Submission submission) {
      return submission.getSamples();
    }

    @Override
    protected String xmlName() {
      return "SAMPLE";
    }

    @Override
    protected String xmlCollectionName() {
      return "SAMPLE_SET";
    }

    @Override
    protected void populate(Element xml, Sample sample) {
      xml.setAttribute("alias", sample.getAlias());

      xml.setAttribute("center_name", submissionProperties.getProperty("submission.centreName"));

      Element sampleTitle = xml.getOwnerDocument().createElementNS(null, "TITLE");
      sampleTitle.setTextContent(sample.getAlias());
      xml.appendChild(sampleTitle);

      Element sampleName = xml.getOwnerDocument().createElementNS(null, "SAMPLE_NAME");
      Element sampleScientificName = xml.getOwnerDocument().createElementNS(null, "SCIENTIFIC_NAME");
      sampleScientificName.setTextContent(sample.getScientificName());
      sampleName.appendChild(sampleScientificName);

      // 2/11/2011 Antony Colles moved IF !=null statement, to help produce valid submission XML.
      Element sampleTaxonIdentifier = xml.getOwnerDocument().createElementNS(null, "TAXON_ID");
      if (!isStringEmptyOrNull(sample.getTaxonIdentifier())) {
        sampleTaxonIdentifier.setTextContent(sample.getTaxonIdentifier());
      } else {
        sampleTaxonIdentifier.setTextContent("000001");
      }
      sampleName.appendChild(sampleTaxonIdentifier);

      xml.appendChild(sampleName);

      Element sampleDescription = xml.getOwnerDocument().createElementNS(null, "DESCRIPTION");
      sampleDescription.setTextContent(sample.getDescription());
      xml.appendChild(sampleDescription);

    }

    @Override
    public String name() {
      return "sample";
    }
  }

  private static final DateFormat DF_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

  private final class DilutionXmlSubfile extends XmlSubmissionFromSet<Entry<Dilution, Partition>> {

    @Override
    protected Set<Entry<Dilution, Partition>> items(Submission submission) {
      return submission.getDilutions().entrySet();
    }

    @Override
    protected String xmlName() {
      return "RUN";
    }

    @Override
    protected String xmlCollectionName() {
      return "RUN_SET";
    }

    @Override
    protected void populate(Element xml, Entry<Dilution, Partition> entry) {
      Run r = entry.getValue().getSequencerPartitionContainer().getLastRun();

      xml.setAttribute("alias", "L00" + entry.getValue().getPartitionNumber() + ":" + entry.getKey().getName() + ":" + r.getAlias());
      xml.setAttribute("run_center", submissionProperties.getProperty("submission.centreName"));
      if (r.getStatus() != null && r.getStatus().getHealth().equals(HealthType.Completed)) {

        xml.setAttribute("run_date", DF_TIMESTAMP.format(r.getStatus().getCompletionDate()));
      }
      xml.setAttribute("center_name", submissionProperties.getProperty("submission.centreName"));

      Collection<Experiment> es = entry.getValue().getPool().getExperiments();
      for (Experiment e : es) {
        Element experimentRef = xml.getOwnerDocument().createElementNS(null, "EXPERIMENT_REF");
        experimentRef.setAttribute("refname", e.getAlias());
        experimentRef.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
        xml.appendChild(experimentRef);
      }

      Element dataBlock = xml.getOwnerDocument().createElementNS(null, "DATA_BLOCK");
      dataBlock.setAttribute("sector", Integer.toString(entry.getValue().getPartitionNumber()));
      if (entry.getValue().getPool().getPoolableElementViews().size() > 1) {
        // multiplexed
        dataBlock.setAttribute("member_name", entry.getKey().getName());
      }

      Element files = xml.getOwnerDocument().createElementNS(null, "FILES");

      try {
        Element file = xml.getOwnerDocument().createElementNS(null, "FILE");
        file.setAttribute("filename",
            r.getAlias() + "/" + "00" + entry.getValue().getPartitionNumber() + "/" + entry.getKey().getName() + "_R1.fastq.gz");
        file.setAttribute("filetype", "fastq");
        file.setAttribute("quality_scoring_system", "phred");
        file.setAttribute("quality_encoding", "ascii");
        file.setAttribute("ascii_offset", "!");
        file.setAttribute("checksum_method", "MD5");
        file.setAttribute("checksum", "");
        Element readLabel = xml.getOwnerDocument().createElementNS(null, "READ_LABEL");
        readLabel.setTextContent("1");
        file.appendChild(readLabel);
        files.appendChild(file);

        if (r.getPairedEnd()) {
          Element file2 = xml.getOwnerDocument().createElementNS(null, "FILE");
          file2.setAttribute("filename",
              r.getAlias() + "/" + "00" + entry.getValue().getPartitionNumber() + "/" + entry.getKey().getName() + "_R2.fastq.gz");
          file2.setAttribute("filetype", "fastq");
          file2.setAttribute("quality_scoring_system", "phred");
          file2.setAttribute("quality_encoding", "ascii");
          file2.setAttribute("ascii_offset", "!");
          file2.setAttribute("checksum_method", "MD5");
          file2.setAttribute("checksum", "");
          Element readLabel2 = xml.getOwnerDocument().createElementNS(null, "READ_LABEL");
          readLabel2.setTextContent("2");
          file2.appendChild(readLabel2);
          files.appendChild(file2);
        }
      } catch (Exception e) {
        log.error("build submission", e);
      }

    }

    @Override
    public String name() {
      return "run";
    }
  }

  private abstract class ChildSubmissionFile {
    public abstract void generateDocument(MultipartEntity entity, Submission submission, Properties submissionProperties)
        throws ParserConfigurationException;

    public abstract String name();

    public abstract boolean isEmpty(Submission submission);

    public String fileName(Submission submission) {
      return String.format("%s_%s_%s.xml", submission.getName(), name(), DF_TIMESTAMP.format(submission.getSubmissionDate()));
    }
  }

  private abstract class XmlSubmissionFromSet<T> extends ChildSubmissionFile {
    protected abstract Set<T> items(Submission submission);

    protected abstract String xmlName();

    protected abstract String xmlCollectionName();

    @Override
    public boolean isEmpty(Submission submission) {
      return items(submission).isEmpty();
    }

    @Override
    public void generateDocument(MultipartEntity entity, Submission submission, Properties submissionProperties)
        throws ParserConfigurationException {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      Element set = document.createElementNS(null, xmlCollectionName());
      document.appendChild(set);
      for (T item : items(submission)) {
        Element itemElement = document.createElement(xmlName());
        populate(itemElement, item);
        set.appendChild(itemElement);
      }

    }

    protected abstract void populate(Element itemElement, T item);

  }

  private final ChildSubmissionFile[] FILES = new ChildSubmissionFile[] { new StudyXmlSubfile(), new SampleXmlSubfile(),
      new DilutionXmlSubfile(),
      new ExperimentXmlSubfile() };

  @Override
  public MultipartEntity prepareSubmission(Submission submission) throws SubmissionException {
    try {
      MultipartEntity entity = new MultipartEntity();
      submission.setSubmissionDate(new Date());

      Document submissionDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      Element s = submissionDocument.createElementNS(null, "SUBMISSION");
      s.setAttribute("alias", submission.getAlias());
      s.setAttribute("submission_date", DF_TIMESTAMP.format(submission.getSubmissionDate()));
      s.setAttribute("submission_comment", submission.getDescription());
      s.setAttribute("center_name", submissionProperties.getProperty("submission.centreName"));

      Element title = submissionDocument.createElementNS(null, "TITLE");
      title.setTextContent(submission.getTitle());
      s.appendChild(title);

      Element contacts = submissionDocument.createElementNS(null, "CONTACTS");
      for (String contactName : submissionProperties.getProperty("submission.contacts").split(",")) {
        Element contact = submissionDocument.createElementNS(null, "CONTACT");
        contact.setAttribute("name", contactName);
        contacts.appendChild(contact);
      }
      s.appendChild(contacts);

      SubmissionActionType sat = submission.getSubmissionActionType();

      Element actions = submissionDocument.createElementNS(null, "ACTIONS");

      for (ChildSubmissionFile subFile : FILES) {

        subFile.generateDocument(entity, submission, submissionProperties);
        if (!subFile.isEmpty(submission)) {
          if (sat == SubmissionActionType.ADD || sat == SubmissionActionType.VALIDATE) {
            Element action = submissionDocument.createElementNS(null, "ACTION");
            Element validate = submissionDocument.createElementNS(null, sat.name());
            validate.setAttribute("schema", subFile.name());
            validate.setAttribute("source", subFile.fileName(submission));
            action.appendChild(validate);
            actions.appendChild(action);
          }
        }
      }
      s.appendChild(actions);

      if (submissionDocument.getElementsByTagName("SUBMISSION_SET").item(0) != null) {
        submissionDocument.getElementsByTagName("SUBMISSION_SET").item(0).appendChild(s);
      } else {
        Element submissionSet = submissionDocument.createElementNS(null, "SUBMISSION_SET");
        submissionDocument.appendChild(submissionSet);
        submissionSet.appendChild(s);
      }

      entity.addPart("SUBMISSION",
          new StringBody(SubmissionUtils.transform(submissionDocument)));

      return entity;

    } catch (ParserConfigurationException e) {
      log.error("generate submission metadata", e);
      throw new SubmissionException(e.getMessage());
    } catch (TransformerException e) {
      log.error("generate submission metadata", e);
      throw new SubmissionException(e.getMessage());
    } catch (IOException e) {
      log.error("generate submission metadata", e);
      throw new SubmissionException(
          "Cannot generate data.");
    }
  }

  /**
   * Submits the given set of Submittables to the ERA submission service endpoint
   * 
   * @param submissionData
   *          of type Set<Submittable>
   * @return Document
   * @throws SubmissionException
   *           when an error occurred with the submission process
   */
  @Override
  public Document submit(Submission submission) throws SubmissionException {
    try {
      MultipartEntity request = prepareSubmission(submission);

      String url = getSubmissionEndPoint() + "?auth=ERA%20" + dropBox + "%20" + authKey;
      HttpClient httpclient = getEvilTrustingTrustManager(new DefaultHttpClient());
      HttpPost httppost = new HttpPost(url);

      httppost.setEntity(request);
      HttpResponse response = httpclient.execute(httppost);

      if (response.getStatusLine().getStatusCode() != 200) {
        throw new SubmissionException(
            "Response from submission endpoint (" + url + ") was not OK (200). Was: " + response.getStatusLine().getStatusCode());
      }
      HttpEntity resEntity = response.getEntity();
      Document submissionReport = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
      SubmissionUtils.transform(resEntity.getContent(), submissionReport);
      File savedReport = new File(
          submissionStoragePath + submission.getName() + File.separator + "report_" + DF_TIMESTAMP.format(new Date()) + ".xml");
      SubmissionUtils.transform(submissionReport, savedReport);
      return submissionReport;
    } catch (IOException | TransformerException | ParserConfigurationException e) {
      log.error("submission report", e);
    }
    return null;
  }

  @Override
  public Map<String, Object> parseResponse(Document report) {
    Map<String, Object> responseMap = new HashMap<>();
    NodeList errors = report.getElementsByTagName("ERROR");
    NodeList infos = report.getElementsByTagName("INFO");

    ArrayList<String> errorList = new ArrayList<>();
    if (errors.getLength() > 0) {
      for (int i = 0; i < errors.getLength(); i++) {
        errorList.add(errors.item(i).getTextContent());
      }
      responseMap.put("errors", JSONArray.fromObject(errorList));
    }

    ArrayList<String> infoList = new ArrayList<>();
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
    Set<File> dataFiles = new HashSet<>();
    FilePathGenerator FPG = new TGACIlluminaFilepathGenerator();

    for (Object o : s.getDilutions().values()) {
      Partition l = (Partition) o;
      try {
        dataFiles = FPG.generateFilePaths(l);
      } catch (SubmissionException submissionException) {
        log.error("submit sequence data", submissionException);
      }
    }

    if (dataFiles.size() > 0) {
      TransferMethod t = new FTPTransferMethod();

      try {
        UploadReport report = t.uploadSequenceData(dataFiles, URI.create("ftp://localhost"));
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

  // TODO(apmasell): These files no longer get written to disk, so this method doesn't work.
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
            Date test = DF_TIMESTAMP.parse(d);
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
        dateStr = "_" + DF_TIMESTAMP.format(latestDate);
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
    } catch (Exception t) {
      log.error("Something nasty happened with the EvilTrustingTrustManager. Warranty is null and void!", t);
      return null;
    }
  }
}
