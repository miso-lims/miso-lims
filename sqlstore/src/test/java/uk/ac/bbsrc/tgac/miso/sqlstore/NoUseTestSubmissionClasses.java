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

package uk.ac.bbsrc.tgac.miso.sqlstore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.EraExperimentDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.EraSampleDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.EraStudyDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubmissionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;

/**
 * uk.ac.bbsrc.tgac.miso.sqlstore Tests the MISO Submission classes using data from the dev DB. Creates submission, study, experiment and
 * run XMLs and validates them against the appropriate SRA XSD.
 * <p/>
 * Info
 *
 * @author Rob Davey & Antony Colles
 * @since 0.0.2
 */

public class NoUseTestSubmissionClasses extends LimsDAOTestCase {
  protected static final Logger log = LoggerFactory.getLogger(NoUseTestSubmissionClasses.class);
  protected static String xsdPath = "/storage/miso/xsd/";
  protected static String xmlPath = "/storage/miso/xml/";

  Collection<Study> studies;
  Collection<Sample> samples;
  Collection<Experiment> experiments;
  List<Run> runs;

  Project project;
  Submission submission;
  Study study;
  Sample sample;
  Experiment experiment;
  Run run;

  DocumentBuilder docBuilder;
  Document submissionDocument;

  @Override
  @Before
  public void setUp() {
    try {
      super.setUp();
      System.out.println("Super setup");
    } catch (Exception e) {
      e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
    }
    System.out.println("Child setup");
  }

  public void test() {
    try {
      project = getProjectDAO().get(99);
      docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      studies = getStudyDAO().listByProjectId(project.getProjectId());
      samples = getSampleDAO().listByProjectId(project.getProjectId());
      runs = getRunDAO().listByProjectId(project.getProjectId());

      // initialising upload XML creation
      String remotePath = "remotePath";
      Element file;
      FileInputStream fis;
      String md5;

      Document upload = docBuilder.newDocument();
      Element up = upload.createElement("UPLOAD");
      // sets user and date attributes
      up.setAttribute("initiatedBy", "Fred");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      up.setAttribute("submissionDate", df.format(new Date()));
      upload.appendChild(up);
      // adds Sequence data node
      Element seqDat = upload.createElementNS(null, "SEQUENCEDATA");
      up.appendChild(seqDat);
      file = upload.createElement("FILE");
      file.setAttribute("localPath", "Local/path/sequence1.fsq");
      file.setAttribute("remotePath", "remote/path/sequence1.fsq");

      /*
       * MD5 check in progress Antony 23/11/2011 MessageDigest md = new MessageDigest.getInstance("MD5"); md.
       */

      Element metaData = upload.createElement("METADATA");
      up.appendChild(metaData);

      // file element has attributes set and is then appended to metadata with each file creation

      // Initialising submission XML creation
      submission = new SubmissionImpl();
      submission.setName(project.getProjectId() + "submission");
      submission.setAlias(project.getProjectId() + "SubAlias");
      submission.setSubmissionActionType(SubmissionActionType.ADD);

      // prints Project and sub-item details to console
      System.out.println("Testing Project: " + project.getName());
      System.out.println("No of studies: " + studies.size());
      System.out.println("No of samples: " + samples.size());
      System.out.println("No of runs: " + runs.size());

      // creates study XML, saves it to file and then validates
      System.out.println("Validating XML for studies:");
      submissionDocument = docBuilder.newDocument();

      for (Study study : studies) {
        submission.addSubmissionElement(study);
        new EraStudyDecorator(study, new Properties(), submissionDocument).buildSubmission();
      }
      String studyFileName = xmlPath + project.getProjectId() + "study.xml";
      SubmissionUtils.transform(submissionDocument, new File(studyFileName));
      validateXML(studyFileName, xsdPath + "SRA.study.xsd");

      fis = new FileInputStream(new File(studyFileName));
      md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);

      System.out.println("completed validation of " + studyFileName);
      file = upload.createElement("FILE");
      file.setAttribute("localPath", studyFileName);
      file.setAttribute("md5", md5);
      metaData.appendChild(file);
      submissionDocument = null;

      for (Study study : studies) {
        // retrieves experiments related to this study
        experiments = getExperimentDAO().listByStudyId(study.getId());
        System.out.println("No of Experiments: " + experiments.size());

        // builds experiment xml
        System.out.println("Validating XML for experiments");
        submissionDocument = docBuilder.newDocument();

        String experimentFileName = xmlPath + project.getProjectId() + study.getName() + "experiment.xml";

        for (Experiment experiment : experiments) {
          new EraExperimentDecorator(experiment, new Properties(), submissionDocument).buildSubmission();
          submission.addSubmissionElement(experiment);
        }

        SubmissionUtils.transform(submissionDocument, new File(experimentFileName));
        validateXML(experimentFileName, xsdPath + "SRA.experiment.xsd");
        System.out.println("completed validation of " + experimentFileName);
        file = upload.createElement("FILE");
        file.setAttribute("localPath", experimentFileName);
        fis = new FileInputStream(new File(experimentFileName));
        md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
        file.setAttribute("md5", md5);
        metaData.appendChild(file);
        submissionDocument = null;
      }

      // Gets all samples associated with the project
      System.out.println("No of Samples: " + samples.size());
      System.out.println("Validating XML for samples ");
      submissionDocument = docBuilder.newDocument();
      String sampleFileName = xmlPath + project.getProjectId() + "sample.xml";

      for (Sample sample : samples) {
        System.out.println(sample.getName() + sample.getAlias());
        new EraSampleDecorator(sample, new Properties(), submissionDocument).buildSubmission();
        submission.addSubmissionElement(sample);
      }

      SubmissionUtils.transform(submissionDocument, new File(sampleFileName));
      validateXML(sampleFileName, xsdPath + "SRA.sample.xsd");
      System.out.println("completed validation of " + sampleFileName);
      file = upload.createElement("FILE");
      fis = new FileInputStream(new File(sampleFileName));
      md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
      file.setAttribute("md5", md5);

      file.setAttribute("localPath", sampleFileName);
      metaData.appendChild(file);

      submissionDocument = null;
      // temporarily commented 23/11/2011 by Antony due to know file issue, and to save time testing rest of class
      // loops recursively through MISO runs and the chambers within them, adding each to the run xml.
      submissionDocument = docBuilder.newDocument();

      submissionDocument = null;

      SubmissionUtils.transform(upload, new File(xmlPath + project.getProjectId() + "upload.xml"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void validateXML(String xmlFile, String schema) throws Exception {
    // Doesn't use the validator class like the other methods tried, but seems to work, ie
    // picks up errors in XML documents.
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

      factory.setValidating(false);
      factory.setNamespaceAware(true);
      // create a SchemaFactory
      SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

      factory.setSchema(schemaFactory.newSchema(new Source[] { new StreamSource(schema) }));

      DocumentBuilder builder = factory.newDocumentBuilder();
      File newFile = new File(xmlFile);
      builder.parse(newFile);
      System.out.println("Validation of " + xmlFile + " complete!");
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
