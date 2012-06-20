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
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaRun;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Run;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidRun;
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
  public static void generateSubmissionXML(Document doc, Collection submittables, String submittableType) {
    if (submittableType.equals("study")) {
      generateStudySubmissionXML(doc, submittables);
    }
    else if (submittableType.equals("sample")) {
      generateSampleSubmissionXML(doc, submittables);
    }
    else if (submittableType.equals("experiment")) {
      generateExperimentSubmissionXML(doc, submittables);
    }
    else if (submittableType.equals("run")) {
      generatePartitionRunSubmissionXML(doc, submittables);
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
  public static void generateParentSubmissionXML(Document doc, Submission submission) {
    new EraSubmissionDecorator(submission, doc).buildSubmission();  
  }

 /**
   * Generate a Study XML fragment from a Study object and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param s of type Study
   */
  public static void generateStudySubmissionXML(Document doc, Study s) {
    new EraStudyDecorator(s, doc).buildSubmission();
  }

  /**
   * Generate a Study XML fragment from a collection of Study objects and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param c of type Collection<Study>
   */
  public static void generateStudySubmissionXML(Document doc, Collection<Study> c) {
    if (c.size() > 1) {
      Element set = doc.createElementNS(null, "STUDY_SET");
      doc.appendChild(set);
    }

    for (Study s : c) {
      generateStudySubmissionXML(doc, s);
    }
  }

 /**
   * Generate an Experiment XML fragment from an Experiment object and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param e of type Experiment
   */
  public static void generateExperimentSubmissionXML(Document doc, Experiment e) {
    new EraExperimentDecorator(e, doc).buildSubmission();  
  }

  /**
   * Generate an Experiment XML fragment from a collection of Experiment objects and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param c of type Collection<Experiment>
   */
  public static void generateExperimentSubmissionXML(Document doc, Collection<Experiment> c) {
    if (c.size() > 1) {
      Element set = doc.createElementNS(null, "EXPERIMENT_SET");
      doc.appendChild(set);
    }

    for (Experiment e : c) {
      generateExperimentSubmissionXML(doc, e);
    }
  }

 /**
   * Generate a Sample XML fragment from a Sample object and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param s of type Sample
   */
  public static void generateSampleSubmissionXML(Document doc, Sample s) {
    new EraSampleDecorator(s, doc).buildSubmission();
  }

  /**
   * Generate a Sample XML fragment from a collection of Sample objects and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param c of type Collection<Sample>
   */
  public static void generateSampleSubmissionXML(Document doc, Collection<Sample> c) {
    if (c.size() > 1) {
      Element set = doc.createElementNS(null, "SAMPLE_SET");
      doc.appendChild(set);
    }

    for (Sample s : c) {
      generateSampleSubmissionXML(doc, s);
    }
  }

 /**
   * Generate a Run XML fragment from a SequencerExperimentPartition object (that is present on the given Run object)
   * and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param p of type SequencerExperimentPartition
   */
  public static void generatePartitionRunSubmissionXML(Document doc, SequencerPoolPartition p) {
    new EraRunDecorator(p, doc).buildSubmission();
  }

 /**
   * Generate a Run XML fragment from a SequencerExperimentPartition object (that is present on the given Run object)
   * and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param p of type SequencerExperimentPartition
   * @param r of type Run
   */
  public static void generatePartitionRunSubmissionXML(Document doc, SequencerPoolPartition p, Run r) {
    new EraRunDecorator(p, r, doc).buildSubmission();
  }

  /**
   * Generate a Run XML fragment from a collection of SequencerPoolPartition objects and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param c of type Collection<SequencerPoolPartition>
   */
  public static void generatePartitionRunSubmissionXML(Document doc, Collection<SequencerPoolPartition> c) {
    if (c.size() > 1) {
      Element set = doc.createElementNS(null, "RUN_SET");
      doc.appendChild(set);
    }

    for (SequencerPoolPartition p : c) {
      generatePartitionRunSubmissionXML(doc, p);
    }
  }

  /**
   * Generate a Run XML fragment from a Run object and pipe the results into the supplied DOM XML Document
   *
   * @param doc of type Document
   * @param r of type Run
   */
  public static void generateFullRunSubmissionXML(Document doc, Run r) {

    Element runSet = doc.createElementNS(null, "RUN_SET");
    doc.appendChild(runSet);

    for (SequencerPartitionContainer<SequencerPoolPartition> f : ((RunImpl)r).getSequencerPartitionContainers()) {
      for (SequencerPoolPartition p : f.getPartitions()) {
        if (p.getPool() != null) {
          generatePartitionRunSubmissionXML(doc, p, r);
        }
      }
    }

    /*
    if (r instanceof IlluminaRun) {
      for (Flowcell f : ((IlluminaRun) r).getFlowcells()) {
        for (SequencerPoolPartition l : ((LaneFlowcell) f).getPartitions()) {
          if (l.getPool() != null) {
            generatePartitionRunSubmissionXML(doc, l);
          }
        }
      }
    }
    else if (r instanceof LS454Run) {
      for (Flowcell f : ((LS454Run) r).getFlowcells()) {
        for (Chamber c : ((ChamberFlowcell) f).getPartitions()) {
          if (c.getPool() != null) {
            generatePartitionRunSubmissionXML(doc, c);
          }
        }
      }
    }
    else if (r instanceof SolidRun) {
      for (Flowcell f : ((SolidRun) r).getFlowcells()) {
        for (Chamber c : ((ChamberFlowcell) f).getPartitions()) {
          if (c.getPool() != null) {
            generatePartitionRunSubmissionXML(doc, c);
          }
        }
      }
    }
    else {

    }
    */
  }
}
