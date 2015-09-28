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

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;

import javax.xml.parsers.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;


/**
 * uk.ac.bbsrc.tgac.miso.sqlstore
 * Tests the MISO Submission classes using data from the dev DB.
 * Creates submission, study, experiment and run XMLs and validates them against the appropriate SRA XSD.
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
  //List<Flowcell> flowcells;

  Project project;
  Submission submission;
  Study study;
  Sample sample;
  Experiment experiment;
  Run run;

  DocumentBuilder docBuilder;
  Document submissionDocument;

  @Before
  public void setUp() {
    try {
      super.setUp();
      System.out.println("Super setup");
    }
    catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    System.out.println("Child setup");
  }

  //@Test

  /*
 temporary method for listing all platforms
 public void lll() throws Exception {
     List<Platform> lsts=getPlatformDAO().listAll();
     for(Platform l:lsts){
     System.out.println(l.getPlatformType()+" "+l.getNameAndModel()+" "+l.getDescription());
     }
 }
  */
  /* temporary method for listing all libraryStrategyTypes
  public void lll() throws Exception {
      List<LibraryStrategyType> lsts=getLibraryDAO().listAllLibraryStrategyTypes();
      for(LibraryStrategyType l:lsts){
      System.out.println(l.getLibraryStrategyTypeId()+" "+l.getName()+" "+l.getDescription());
      }
  }
  */

  public void test() {
    try {
      project = getProjectDAO().get(99);
      docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      studies = getStudyDAO().listByProjectId(project.getProjectId());
      samples = getSampleDAO().listByProjectId(project.getProjectId());
      runs = getRunDAO().listByProjectId(project.getProjectId());

      //initialising upload XML creation
      String remotePath = "remotePath";
      Element file;
      FileInputStream fis;
      String md5;

      Document upload = docBuilder.newDocument();
      Element up = upload.createElement("UPLOAD");
      //sets user and date attributes
      up.setAttribute("initiatedBy", "Fred");
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      up.setAttribute("submissionDate", df.format(new Date()));
      upload.appendChild(up);
      //adds Sequence data node
      Element seqDat = upload.createElementNS(null, "SEQUENCEDATA");
      up.appendChild(seqDat);
      file = upload.createElement("FILE");
      file.setAttribute("localPath", "Local/path/sequence1.fsq");
      file.setAttribute("remotePath", "remote/path/sequence1.fsq");

      /* MD5 check in progress Antony 23/11/2011
      MessageDigest md = new MessageDigest.getInstance("MD5");
      md.
      */

      Element metaData = upload.createElement("METADATA");
      up.appendChild(metaData);

      //file element has attributes set and is then appended to metadata with each file creation

      //Initialising submission XML creation
      submission = new SubmissionImpl();
      submission.setName(project.getProjectId() + "submission");
      submission.setAlias(project.getProjectId() + "SubAlias");
      submission.setSubmissionActionType(SubmissionActionType.ADD);

      //prints Project and sub-item details to console
      System.out.println("Testing Project: " + project.getName());
      System.out.println("No of studies: " + studies.size());
      System.out.println("No of samples: " + samples.size());
      System.out.println("No of runs: " + runs.size());

      //creates study XML, saves it to file and then validates
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
        //retrieves experiments related to this study
        experiments = getExperimentDAO().listByStudyId(study.getId());
        System.out.println("No of Experiments: " + experiments.size());

        //builds experiment xml
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

      //Gets all samples associated with the project
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
      //  temporarily commented 23/11/2011 by Antony due to know file issue, and to save time testing rest of class
      //loops recursively through MISO runs and the chambers within them, adding each to the run xml.
      submissionDocument = docBuilder.newDocument();

      /*
      for (Run run : runs) {
        flowcells = getFlowcellDAO().listAllFlowcellsByRunId(run.getRunId());

        PlatformType platform = run.getPlatformType();
        System.out.println("Platform:" + platform);
        System.out.println("Number of flowcells:" + flowcells.size());

        if (platform.equals(PlatformType.ILLUMINA)) {
          for (Flowcell flowcell : flowcells) {
            Collection<Lane> lanes = getLaneDAO().listByFlowcellId(flowcell.getFlowcellId());
            //System.out.println("fstudylowcell: "+flowcell.getFlowcellId());
            //System.out.println("Number of partitions:"+lanes.size());
            for (Lane lane : lanes) {
              if (lane.getPool() != null) {
                System.out.println("Adding " + lane.getId() + " to run.xml");
                new EraRunDecorator(lane, submissionDocument).buildSubmission();
                submission.addSubmissionElement(lane);
              }
            }
          }
        }
        else {
          for (Flowcell flowcell : flowcells) {
            Collection<Chamber> chambers = getChamberDAO().listByFlowcellId(flowcell.getFlowcellId());
            //System.out.println("Number of partitions:"+chambers.size());
            for (Chamber chamber : chambers) {
              if (chamber.getPool() != null) {
                System.out.println("Adding " + chamber.getId() + " to run.xml");
                new EraRunDecorator(chamber, submissionDocument).buildSubmission();
                submission.addSubmissionElement(chamber);
              }
            }
          }
        }
      }

      String chamberFileName = xmlPath + project.getProjectId() + "run.xml";
      SubmissionUtils.transform(submissionDocument, new File(chamberFileName));
      validateXML(chamberFileName, xsdPath + "SRA.run.xsd");
      System.out.println("completed validation of " + chamberFileName);
      file = upload.createElement("FILE");
      fis = new FileInputStream(new File(sampleFileName));
      md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
      file.setAttribute("md5", md5);

      file.setAttribute("localPath", sampleFileName);
      metaData.appendChild(file);
      submissionDocument = null;

      System.out.println("Validating XML for submission: " + submission.getName());
      submissionDocument = docBuilder.newDocument();
      new EraSubmissionDecorator(submission, submissionDocument).buildSubmission();
      String submissionFileName = xmlPath + submission.getName() + ".xml";
      SubmissionUtils.transform(submissionDocument, new File(submissionFileName));
      validateXML(submissionFileName, xsdPath + "SRA.submission.xsd");
      file = upload.createElement("FILE");
      file.setAttribute("localPath", submissionFileName);
      fis = new FileInputStream(new File(submissionFileName));
      md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
      file.setAttribute("md5", md5);
      metaData.appendChild(file);
      */
      submissionDocument = null;

      SubmissionUtils.transform(upload, new File(xmlPath + project.getProjectId() + "upload.xml"));
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void validateXML(String xmlFile, String schema) throws Exception {
    //Doesn't use the validator class like the other methods tried, but seems to work, ie
    //picks up errors in XML documents.
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

      factory.setValidating(false);
      factory.setNamespaceAware(true);
      // create a SchemaFactory
      SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

      factory.setSchema(schemaFactory.newSchema(new Source[]{new StreamSource(schema)}));

      DocumentBuilder builder = factory.newDocumentBuilder();
      // builder.setErrorHandler(new SimpleErrorHandler());
      File newFile = new File(xmlFile);
      builder.parse(newFile);
      System.out.println("Validation of " + xmlFile + " complete!");
    }
    catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    catch (SAXException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
}