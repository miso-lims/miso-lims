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
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.EraExperimentDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.EraRunDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.EraSampleDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.EraStudyDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.EraSubmissionDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SubmissionImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.util.SubmissionUtils;

/**
 * Created by IntelliJ IDEA. User: collesa Date: 18/11/11 Time: 10:14 To change this template use File | Settings | File Templates.
 */
public class SubmissionAutomator extends LimsDAOTestCase {
  protected static final Logger log = LoggerFactory.getLogger(SubmissionAutomator.class);

  protected static String xsdPath = "/storage/miso/xsd/";
  protected static String xmlPath = "/storage/miso/xml/";

  public void submitProject(Project p, User u) {
    try {
      super.setUp();
      log.debug("Super setup");
    } catch (Exception e) {
      log.error("super setup", e);
    }
    log.debug("Child setup");

    try {
      DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Collection<Study> studies = getStudyDAO().listByProjectId(p.getProjectId());
      Collection<Sample> samples = getSampleDAO().listByProjectId(p.getProjectId());
      List<Run> runs = getRunDAO().listByProjectId(p.getProjectId());

      // prints Project and sub-item details to console
      log.info("Testing Project: " + p.getName());
      log.info("No of studies: " + studies.size());
      log.info("No of samples: " + samples.size());
      log.info("No of runs: " + runs.size());

      Document submissionDocument;
      for (Study study : studies) {
        // creates study XMLs, saves them to file and then validates
        log.debug("Validating XML for study: " + study.getName());
        submissionDocument = docBuilder.newDocument();
        new EraStudyDecorator(study, new Properties(), submissionDocument).buildSubmission();
        String studyFileName = xmlPath + p.getProjectId() + study.getName() + ".xml";
        SubmissionUtils.transform(submissionDocument, new File(studyFileName));
        validateXML(studyFileName, xsdPath + "SRA.study.xsd");
        log.debug("completed validation of " + studyFileName);
        submissionDocument = null;

        // retrieves experiments related to this study
        Collection<Experiment> experiments = getExperimentDAO().listByStudyId(study.getId());
        log.info("No of Experiments: " + experiments.size());

        for (Experiment experiment : experiments) { // creates experiment XML
          log.debug("Validating XML for experiment: " + experiment.getName());
          submissionDocument = docBuilder.newDocument();
          new EraExperimentDecorator(experiment, new Properties(), submissionDocument).buildSubmission();
          String experimentFileName = xmlPath + p.getProjectId() + study.getName() + experiment.getName() + ".xml";
          SubmissionUtils.transform(submissionDocument, new File(experimentFileName));
          validateXML(experimentFileName, xsdPath + "SRA.experiment.xsd");
          log.debug("completed validation of " + experimentFileName);
          submissionDocument = null;
        }
      }

      // Gets all samples associated with the project
      for (Sample sample : samples) { // creates sample XML
        log.debug("Validating XML for sample: " + sample.getName());
        submissionDocument = docBuilder.newDocument();
        new EraSampleDecorator(sample, new Properties(), submissionDocument).buildSubmission();
        String sampleFileName = xmlPath + p.getProjectId() + sample.getName() + ".xml";
        SubmissionUtils.transform(submissionDocument, new File(sampleFileName));
        validateXML(sampleFileName, xsdPath + "SRA.sample.xsd");
        log.debug("completed validation of " + sampleFileName);
        submissionDocument = null;
      }
      // loops recursively through MISO runs and the chambers within them, building and validating SRA run xmls for each.
      for (Run run : runs) { // creates run XMLs from either chamber or lane objects
        log.debug("Validating XMLs for run: " + run.getName());
        List<SequencerPartitionContainer<SequencerPoolPartition>> containers = getSequencerPartitionContainerDAO()
            .listAllSequencerPartitionContainersByRunId(run.getId());

        PlatformType platform = run.getPlatformType();
        log.debug("Platform:" + platform);
        log.debug("Number of containers:" + containers.size());

        for (SequencerPartitionContainer<SequencerPoolPartition> container : containers) {
          Collection<SequencerPoolPartition> partitions = getPartitionDAO().listBySequencerPartitionContainerId(container.getId());
          log.debug("container: " + container.getId());
          log.debug("Number of partitions:" + partitions.size());
          for (SequencerPoolPartition partition : partitions) {
            log.debug("Validating XML for partition: " + partition.getId());
            log.debug("Pool: " + partition.getPool());

            if (partition.getPool() != null) {
              log.debug("Experiments:" + partition.getPool().getExperiments());
              submissionDocument = docBuilder.newDocument();
              new EraRunDecorator(partition, new Properties(), submissionDocument).buildSubmission();
              String chamberFileName = xmlPath + p.getProjectId() + run.getName() + "FC" + container.getId() + "Lane" + partition.getId()
                  + ".xml";
              SubmissionUtils.transform(submissionDocument, new File(chamberFileName));
              validateXML(chamberFileName, xsdPath + "SRA.run.xsd");
              log.debug("completed validation of " + chamberFileName);
              submissionDocument = null;
            }
          }
        }
      }

      // creates submission xml
      Submission submission = new SubmissionImpl();
      submission.setName("testSub");
      submission.setAlias("testSubALias");
      submission.setSubmissionActionType(SubmissionActionType.ADD);

      Study study1 = getStudyDAO().get(5);
      Sample sample1 = getSampleDAO().get(393);
      Run run1 = getRunDAO().get(165);
      Experiment experiment1 = getExperimentDAO().get(16);

      submission.addSubmissionElement(study1);
      submission.addSubmissionElement(sample1);
      submission.addSubmissionElement(run1);
      submission.addSubmissionElement(experiment1);

      log.debug("Validating XML for submission: " + submission.getName());
      submissionDocument = docBuilder.newDocument();
      new EraSubmissionDecorator(submission, new Properties(), submissionDocument).buildSubmission();
      String submissionFileName = xmlPath + submission.getName() + ".xml";
      SubmissionUtils.transform(submissionDocument, new File(submissionFileName));
      validateXML(submissionFileName, xsdPath + "SRA.submission.xsd");
      submissionDocument = null;
    } catch (Exception e) {
      log.error("submit project", e);
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
      log.info("Validation of " + xmlFile + " complete!");
    } catch (ParserConfigurationException e) {
      log.error("validate XML", e);
    } catch (SAXException e) {
      log.error("validate XML", e);
    } catch (IOException e) {
      log.error("validate XML", e);
    }
  }
}
