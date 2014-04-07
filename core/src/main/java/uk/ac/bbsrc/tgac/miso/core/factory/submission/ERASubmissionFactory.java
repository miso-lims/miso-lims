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

package uk.ac.bbsrc.tgac.miso.core.factory.submission;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import java.util.*;

/**
 * Generates XML fragments for Submission, Study, Sample, Experiment and Run schema datatypes based on the SRA submission schema, backed by the MISO data model objects 
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class ERASubmissionFactory {
  /**
   * Generate a SRA XML fragment from a homogenous collection of Submittable elements
   *
   * @param doc of type Document
   * @param submittables of type Collection
   * @param submittableType of type String
   */
  public static void generateSubmissionXML(Document doc, Collection submittables, String submittableType, Properties submissionProperties) {
    if (submittableType.equals("study")) {
      generateStudySubmissionXML(doc, submittables, submissionProperties);
    }
    else if (submittableType.equals("sample")) {
      generateSampleSubmissionXML(doc, submittables, submissionProperties);
    }
    else if (submittableType.equals("experiment")) {
      generateExperimentSubmissionXML(doc, submittables, submissionProperties);
    }
    else if (submittableType.equals("run")) {
      generatePartitionRunSubmissionXML(doc, submittables, submissionProperties);
    }
    else {

    }
  }

  /**
   * Generate a Submission XML fragment from a Submission object and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param submission of type SubmissionImpl
   */
  public static void generateParentSubmissionXML(Document doc, Submission submission, Properties submissionProperties) {
    new EraSubmissionDecorator(submission, submissionProperties, doc).buildSubmission();
  }

 /**
   * Generate a Study XML fragment from a Study object and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param s of type Study
   */
  public static void generateStudySubmissionXML(Document doc, Study s, Properties submissionProperties) {
    new EraStudyDecorator(s, submissionProperties, doc).buildSubmission();
  }

  /**
   * Generate a Study XML fragment from a collection of Study objects and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param c of type Collection<Study>
   */
  public static void generateStudySubmissionXML(Document doc, Collection<Study> c, Properties submissionProperties) {
    Element set = doc.createElementNS(null, "STUDY_SET");
    doc.appendChild(set);

    for (Study s : c) {
      generateStudySubmissionXML(doc, s, submissionProperties);
    }
  }

 /**
   * Generate an Experiment XML fragment from an Experiment object and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param e of type Experiment
   */
  public static void generateExperimentSubmissionXML(Document doc, Experiment e, Properties submissionProperties) {
    new EraExperimentDecorator(e, submissionProperties, doc).buildSubmission();
  }

  /**
   * Generate an Experiment XML fragment from a collection of Experiment objects and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param c of type Collection<Experiment>
   */
  public static void generateExperimentSubmissionXML(Document doc, Collection<Experiment> c, Properties submissionProperties) {
    Element set = doc.createElementNS(null, "EXPERIMENT_SET");
    doc.appendChild(set);

    for (Experiment e : c) {
      generateExperimentSubmissionXML(doc, e, submissionProperties);
    }
  }

 /**
   * Generate a Sample XML fragment from a Sample object and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param s of type Sample
   */
  public static void generateSampleSubmissionXML(Document doc, Sample s, Properties submissionProperties) {
    new EraSampleDecorator(s, submissionProperties, doc).buildSubmission();
  }

  /**
   * Generate a Sample XML fragment from a collection of Sample objects and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param c of type Collection<Sample>
   */
  public static void generateSampleSubmissionXML(Document doc, Collection<Sample> c, Properties submissionProperties) {
    Element set = doc.createElementNS(null, "SAMPLE_SET");
    doc.appendChild(set);

    for (Sample s : c) {
      generateSampleSubmissionXML(doc, s, submissionProperties);
    }
  }

 /**
   * Generate a Run XML fragment from a SequencerExperimentPartition object (that is present on the given Run object)
   * and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param p of type SequencerExperimentPartition
   */
  public static void generatePartitionRunSubmissionXML(Document doc, SequencerPoolPartition p, Properties submissionProperties) {
    new EraRunDecorator(p, submissionProperties, doc).buildSubmission();
  }

 /**
   * Generate a Run XML fragment from a SequencerExperimentPartition object (that is present on the given Run object)
   * and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param p of type SequencerExperimentPartition
   * @param r of type Run
   */
  public static void generatePartitionRunSubmissionXML(Document doc, SequencerPoolPartition p, Run r, Properties submissionProperties) {
    new EraRunDecorator(p, r, submissionProperties, doc).buildSubmission();
  }

  /**
   * Generate a Run XML fragment from a collection of SequencerPoolPartition objects and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param c of type Collection<SequencerPoolPartition>
   */
  public static void generatePartitionRunSubmissionXML(Document doc, Collection<SequencerPoolPartition> c, Properties submissionProperties) {
    Element set = doc.createElementNS(null, "RUN_SET");
    doc.appendChild(set);

    for (SequencerPoolPartition p : c) {
      generatePartitionRunSubmissionXML(doc, p, submissionProperties);
    }
  }

  /**
   * Generate a Run XML fragment from a Run object and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param r of type Run
   */
  public static void generateFullRunSubmissionXML(Document doc, Run r, Properties submissionProperties) {
    Element runSet = doc.createElementNS(null, "RUN_SET");
    doc.appendChild(runSet);

    for (SequencerPartitionContainer<SequencerPoolPartition> f : ((RunImpl)r).getSequencerPartitionContainers()) {
      for (SequencerPoolPartition p : f.getPartitions()) {
        if (p.getPool() != null) {
          generatePartitionRunSubmissionXML(doc, p, r, submissionProperties);
        }
      }
    }
  }
}
