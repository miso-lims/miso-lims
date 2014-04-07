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

package uk.ac.bbsrc.tgac.miso.core.data.decorator.submission.era;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.AbstractSubmittableDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.TgacSubmissionConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Decorates an Experiment so that an ERA Experiment submission XML document can be built from it
 *
 * @author Rob Davey
 * @date 12-Oct-2010
 * @since 0.0.2
 */
public class EraExperimentDecorator extends AbstractSubmittableDecorator<Document> {

  public EraExperimentDecorator(Submittable submittable, Properties submissionProperties, Document submission) {
    super(submittable, submissionProperties);
    this.submission = submission;
  }

  public void buildSubmission() {
    //submittable.buildSubmission();
    Experiment e = (Experiment)submittable;
    
    if (submission != null) {
      Element experiment = submission.createElementNS(null, "EXPERIMENT");
      experiment.setAttribute("alias", e.getAlias());
      //experiment.setAttribute("accession", e.getAccession());
      experiment.setAttribute("center_name", submissionProperties.getProperty("submission.centreName"));
      //submission.appendChild(experiment);

      Element experimentTitle = submission.createElementNS(null, "TITLE");
      experimentTitle.setTextContent(e.getTitle());
      experiment.appendChild(experimentTitle);

      Element studyRef = submission.createElementNS(null, "STUDY_REF");
      studyRef.setAttribute("refname", e.getStudy().getAlias());
      studyRef.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
      if (e.getStudy().getAccession() != null && !"".equals(e.getStudy().getAccession())) {
        studyRef.setAttribute("accession", e.getStudy().getAccession());
      }
      experiment.appendChild(studyRef);

      Element design = submission.createElementNS(null, "DESIGN");
      experiment.appendChild(design);

      Element designDescription = submission.createElementNS(null, "DESIGN_DESCRIPTION");
      designDescription.setTextContent(e.getDescription());
      design.appendChild(designDescription);

      Element sampleDescriptor = submission.createElementNS(null, "SAMPLE_DESCRIPTOR");
      sampleDescriptor.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));

      Library relevantLibrary = null;

      if (e.getPool() != null) {
        if (e.getPool().getDilutions().size() > 1) {
          //multiplexed pool
          Element pool = submission.createElementNS(null, "POOL");
          sampleDescriptor.appendChild(pool);

          for (Dilution dil : e.getPool().getDilutions()) {
            relevantLibrary = dil.getLibrary();
            Element member = submission.createElementNS(null, "MEMBER");
            member.setAttribute("member_name", dil.getName());
            member.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
            member.setAttribute("refname", relevantLibrary.getSample().getAlias());
            if (relevantLibrary.getSample().getAccession() != null && !"".equals(relevantLibrary.getSample().getAccession())) {
              sampleDescriptor.setAttribute("accession", relevantLibrary.getSample().getAccession());
            }
            pool.appendChild(member);

            Element readLabel = submission.createElementNS(null, "READ_LABEL");
            if (!relevantLibrary.getTagBarcodes().isEmpty()) {
              StringBuilder tsb = new StringBuilder();
              StringBuilder vsb = new StringBuilder();
              for (TagBarcode tb : relevantLibrary.getTagBarcodes().values()) {
                tsb.append(tb.getSequence());
                vsb.append(tb.getName());
              }
              readLabel.setAttribute("read_group_tag", tsb.toString());
              readLabel.setTextContent(vsb.toString());
            }
            member.appendChild(readLabel);
          }
        }
        else {
          for (Dilution dil : e.getPool().getDilutions()) {
            relevantLibrary = dil.getLibrary();
            sampleDescriptor.setAttribute("refname", relevantLibrary.getSample().getAlias());
            sampleDescriptor.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
            if (relevantLibrary.getSample().getAccession() != null && !"".equals(relevantLibrary.getSample().getAccession())) {
              sampleDescriptor.setAttribute("accession", relevantLibrary.getSample().getAccession());
            }
          }
        }
      }

      design.appendChild(sampleDescriptor);

      Element libraryDescriptor = submission.createElementNS(null, "LIBRARY_DESCRIPTOR");
      Element libraryName = submission.createElementNS(null, "LIBRARY_NAME");
      if (relevantLibrary != null) {
        if (e.getPool().getAlias() != null && !"".equals(e.getPool().getAlias())) {
          libraryName.setTextContent(e.getPool().getAlias());
        }
        else {
          libraryName.setTextContent(e.getPool().getName());
        }
      }
      libraryDescriptor.appendChild(libraryName);

      Element libraryStrategy = submission.createElementNS(null, "LIBRARY_STRATEGY");
      if (relevantLibrary != null) {
        libraryStrategy.setTextContent(relevantLibrary.getLibraryStrategyType().getName());
      }
      libraryDescriptor.appendChild(libraryStrategy);

      Element librarySource = submission.createElementNS(null, "LIBRARY_SOURCE");
      if (relevantLibrary != null) {
        librarySource.setTextContent(relevantLibrary.getSample().getSampleType());
      }
      libraryDescriptor.appendChild(librarySource);

      Element librarySelection = submission.createElementNS(null, "LIBRARY_SELECTION");
      if (relevantLibrary != null) {
        librarySelection.setTextContent(relevantLibrary.getLibrarySelectionType().getName());
      }
      libraryDescriptor.appendChild(librarySelection);

      Element libraryLayout = submission.createElementNS(null, "LIBRARY_LAYOUT");
      Element layout;
      if (relevantLibrary != null) {
        if (relevantLibrary.getPaired()) {
          layout = submission.createElementNS(null, "PAIRED");
          if (!relevantLibrary.getLibraryQCs().isEmpty()) {
            List<LibraryQC> qcs = new ArrayList<LibraryQC>(relevantLibrary.getLibraryQCs());
            int insert = 0;
            for (LibraryQC qc : qcs) {
              insert += qc.getInsertSize();
            }
            layout.setAttribute("NOMINAL_LENGTH", String.valueOf(insert/qcs.size()));
          }
          else {
            layout.setAttribute("NOMINAL_LENGTH", "0");
          }
        }
        else {
          layout = submission.createElementNS(null, "SINGLE");
        }
        libraryLayout.appendChild(layout);
      }
      libraryDescriptor.appendChild(libraryLayout);

      Element poolingStrategy = submission.createElementNS(null, "POOLING_STRATEGY");
      if (e.getPool() != null) {
        if (e.getPool().getDilutions().size() > 1) {
          //if (e.getPool().getSpiked()) {
          //  poolingStrategy.setTextContent("spiked library");
          //}          
          poolingStrategy.setTextContent("multiplexed libraries");
        }
        else {
          //if (e.getPool().getSpiked()) {
          //  poolingStrategy.setTextContent("spiked library");
          //}
          poolingStrategy.setTextContent("none");
        }
      }
      libraryDescriptor.appendChild(poolingStrategy);

      design.appendChild(libraryDescriptor);

       // commenting out spot_descriptor to see what happens...
      // SPOT DESCRIPTOR WAS MADE OPTIONAL IN 1.3
      /*
      Element spotDescriptor = submission.createElementNS(null, "SPOT_DESCRIPTOR");
      Element spotDecodeSpec = submission.createElementNS(null, "SPOT_DECODE_SPEC");



      if (e.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
        // |------SEQ1------||------SEQ2------|
        Element spotLength = submission.createElementNS(null, "SPOT_LENGTH");

        spotLength.setTextContent("2");
        spotDecodeSpec.appendChild(spotLength);
        Element readSpec = submission.createElementNS(null, "READ_SPEC");
        spotDecodeSpec.appendChild(readSpec);

        Element readIndex = submission.createElementNS(null, "READ_INDEX");
        readIndex.setTextContent("0");
        readSpec.appendChild(readIndex);
        Element readLabel = submission.createElementNS(null, "READ_LABEL");
        readSpec.appendChild(readLabel);
        Element readClass = submission.createElementNS(null, "READ_CLASS");
        readClass.setTextContent("Application Read");
        readSpec.appendChild(readClass);
        Element readType = submission.createElementNS(null, "READ_TYPE");
        readType.setTextContent("Forward");
        readSpec.appendChild(readType);
        Element ordering = submission.createElementNS(null, "BASE_COORD");
        ordering.setTextContent("1");
        readSpec.appendChild(ordering);



        if (relevantLibrary != null) {
          if (relevantLibrary.getPaired()) {
            Element readSpec2 = submission.createElementNS(null, "READ_SPEC");
            spotDecodeSpec.appendChild(readSpec2);

            Element readIndexP = submission.createElementNS(null, "READ_INDEX");
            readIndexP.setTextContent("1");
            readSpec2.appendChild(readIndexP);
            Element readLabelP = submission.createElementNS(null, "READ_LABEL");

            readSpec2.appendChild(readLabelP);
            Element readClassP = submission.createElementNS(null, "READ_CLASS");
            readClassP.setTextContent("Application Read");
            readSpec2.appendChild(readClassP);
            Element readTypeP = submission.createElementNS(null, "READ_TYPE");
            readTypeP.setTextContent("Reverse");
            readSpec2.appendChild(readTypeP);
            Element orderingP = submission.createElementNS(null, "RELATIVE_ORDER");
            orderingP.setAttribute("follows_read_index", "0");
            readSpec2.appendChild(orderingP);
          }
        }
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
        // |--ADAPTER--||------SEQ1------||--LINKER--||------SEQ2------|
        Element readSpec3 = submission.createElementNS(null, "READ_SPEC");
        spotDecodeSpec.appendChild(readSpec3);

        Element readIndexT1 = submission.createElementNS(null, "READ_INDEX");
        readIndexT1.setTextContent("0");
        readSpec3.appendChild(readIndexT1);
        Element readLabelT1 = submission.createElementNS(null, "READ_LABEL");
        readSpec3.appendChild(readLabelT1);
        Element readClassT1 = submission.createElementNS(null, "READ_CLASS");
        readClassT1.setTextContent("Technical Read");
        readSpec3.appendChild(readClassT1);
        Element readTypeT1 = submission.createElementNS(null, "READ_TYPE");
        readTypeT1.setTextContent("Adapter");
        readSpec3.appendChild(readTypeT1);
        Element orderingT1 = submission.createElementNS(null, "BASE_COORD");
        orderingT1.setTextContent("1");
        readSpec3.appendChild(orderingT1);

        Element readSpec4 = submission.createElementNS(null, "READ_SPEC");
        spotDecodeSpec.appendChild(readSpec4);

        Element readIndexA1 = submission.createElementNS(null, "READ_INDEX");
        readIndexA1.setTextContent("1");
        readSpec4.appendChild(readIndexA1);
        Element readLabelA1 = submission.createElementNS(null, "READ_LABEL");
        readSpec4.appendChild(readLabelA1);
        Element readClassA1 = submission.createElementNS(null, "READ_CLASS");
        readClassA1.setTextContent("Application Read");
        readSpec4.appendChild(readClassA1);
        Element readTypeA1 = submission.createElementNS(null, "READ_TYPE");
        readTypeA1.setTextContent("Forward");
        readSpec4.appendChild(readTypeA1);
        Element orderingA1 = submission.createElementNS(null, "RELATIVE_ORDER");
        orderingA1.setAttribute("follows_read_index", "0");
        readSpec4.appendChild(orderingA1);

        if (relevantLibrary != null) {
          if (relevantLibrary.getPaired()) {
            Element readSpec5 = submission.createElementNS(null, "READ_SPEC");
            spotDecodeSpec.appendChild(readSpec5);

            Element readIndexT2 = submission.createElementNS(null, "READ_INDEX");
            readIndexT2.setTextContent("2");
            readSpec5.appendChild(readIndexT2);
            Element readLabelT2 = submission.createElementNS(null, "READ_LABEL");
            readSpec5.appendChild(readLabelT2);
            Element readClassT2 = submission.createElementNS(null, "READ_CLASS");
            readClassT2.setTextContent("Technical Read");
            readSpec5.appendChild(readClassT2);
            Element readTypeT2 = submission.createElementNS(null, "READ_TYPE");
            readTypeT2.setTextContent("Linker");
            readSpec5.appendChild(readTypeT2);
            Element orderingT2 = submission.createElementNS(null, "RELATIVE_ORDER");
            orderingT2.setAttribute("follows_read_index", "1");
            readSpec5.appendChild(orderingT2);

            Element readSpec6 = submission.createElementNS(null, "READ_SPEC");
            spotDecodeSpec.appendChild(readSpec6);

            Element readIndexA2 = submission.createElementNS(null, "READ_INDEX");
            readIndexA2.setTextContent("3");
            readSpec6.appendChild(readIndexA2);
            Element readLabelA2 = submission.createElementNS(null, "READ_LABEL");
            readSpec6.appendChild(readLabelA2);
            Element readClassA2 = submission.createElementNS(null, "READ_CLASS");
            readClassA2.setTextContent("Application Read");
            readSpec6.appendChild(readClassA2);
            Element readTypeA2 = submission.createElementNS(null, "READ_TYPE");
            readTypeA2.setTextContent("Forward");
            readSpec6.appendChild(readTypeA2);
            Element orderingA2 = submission.createElementNS(null, "RELATIVE_ORDER");
            orderingA2.setAttribute("follows_read_index", "2");
            readSpec6.appendChild(orderingA2);
          }
        }
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
        // |--ADAPTER--||------SEQ1------||--LINKER--||------SEQ2------|
        Element readSpec7 = submission.createElementNS(null, "READ_SPEC");
        spotDecodeSpec.appendChild(readSpec7);

        Element readIndexT1 = submission.createElementNS(null, "READ_INDEX");
        readIndexT1.setTextContent("0");
        readSpec7.appendChild(readIndexT1);
        Element readLabelT1 = submission.createElementNS(null, "READ_LABEL");
        readSpec7.appendChild(readLabelT1);
        Element readClassT1 = submission.createElementNS(null, "READ_CLASS");
        readClassT1.setTextContent("Technical Read");
        readSpec7.appendChild(readClassT1);
        Element readTypeT1 = submission.createElementNS(null, "READ_TYPE");
        readTypeT1.setTextContent("Adapter");
        readSpec7.appendChild(readTypeT1);
        Element orderingT1 = submission.createElementNS(null, "BASE_COORD");
        orderingT1.setTextContent("1");
        readSpec7.appendChild(orderingT1);

        Element readSpec8 = submission.createElementNS(null, "READ_SPEC");
        spotDecodeSpec.appendChild(readSpec8);

        Element readIndexA1 = submission.createElementNS(null, "READ_INDEX");
        readIndexA1.setTextContent("1");
        readSpec8.appendChild(readIndexA1);
        Element readLabelA1 = submission.createElementNS(null, "READ_LABEL");
        readSpec8.appendChild(readLabelA1);
        Element readClassA1 = submission.createElementNS(null, "READ_CLASS");
        readClassA1.setTextContent("Application Read");
        readSpec8.appendChild(readClassA1);
        Element readTypeA1 = submission.createElementNS(null, "READ_TYPE");
        readTypeA1.setTextContent("Forward");
        readSpec8.appendChild(readTypeA1);
        Element orderingA1 = submission.createElementNS(null, "RELATIVE_ORDER");
        orderingA1.setAttribute("follows_read_index", "0");
        readSpec8.appendChild(orderingA1);

        if (relevantLibrary != null) {
          if (relevantLibrary.getPaired()) {
            Element readSpec9 = submission.createElementNS(null, "READ_SPEC");
            spotDecodeSpec.appendChild(readSpec9);

            Element readIndexT2 = submission.createElementNS(null, "READ_INDEX");
            readIndexT2.setTextContent("2");
            readSpec9.appendChild(readIndexT2);
            Element readLabelT2 = submission.createElementNS(null, "READ_LABEL");
            readSpec9.appendChild(readLabelT2);
            Element readClassT2 = submission.createElementNS(null, "READ_CLASS");
            readClassT2.setTextContent("Technical Read");
            readSpec9.appendChild(readClassT2);
            Element readTypeT2 = submission.createElementNS(null, "READ_TYPE");
            readTypeT2.setTextContent("Linker");
            readSpec9.appendChild(readTypeT2);
            Element orderingT2 = submission.createElementNS(null, "BASE_COORD");
            orderingT2.setAttribute("follows_read_index", "1");
            readSpec9.appendChild(orderingT2);

            Element readSpec10 = submission.createElementNS(null, "READ_SPEC");
            spotDecodeSpec.appendChild(readSpec10);

            Element readIndexA2 = submission.createElementNS(null, "READ_INDEX");
            readIndexA2.setTextContent("3");
            readSpec10.appendChild(readIndexA2);
            Element readLabelA2 = submission.createElementNS(null, "READ_LABEL");
            readSpec10.appendChild(readLabelA2);
            Element readClassA2 = submission.createElementNS(null, "READ_CLASS");
            readClassA2.setTextContent("Application Read");
            readSpec10.appendChild(readClassA2);
            Element readTypeA2 = submission.createElementNS(null, "READ_TYPE");
            readTypeA2.setTextContent("Forward");
            readSpec10.appendChild(readTypeA2);
            Element orderingA2 = submission.createElementNS(null, "BASE_COORD");
            orderingA2.setAttribute("follows_read_index", "2");
            readSpec10.appendChild(orderingA2);
          }
        }

      }
      
      spotDescriptor.appendChild(spotDecodeSpec);


      design.appendChild(spotDescriptor);
      */
      if (e.getPlatform() != null) {
        Element platform = submission.createElementNS(null, "PLATFORM");

        if (e.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
          Element type = submission.createElementNS(null, "ILLUMINA");
          platform.appendChild(type);

          Element model = submission.createElementNS(null, "INSTRUMENT_MODEL");
          model.setTextContent(e.getPlatform().getInstrumentModel());
          type.appendChild(model);

//DEPRECATED SRA 1.2          
//          Element cycleCount = submission.createElementNS(null, "CYCLE_COUNT");
//          //illumina 120bp (max 150/160?)
//          cycleCount.setTextContent("120");
//          type.appendChild(cycleCount);

//DEPRECATED SRA 1.5
//          Element sequenceLength = submission.createElementNS(null, "SEQUENCE_LENGTH");
//          sequenceLength.setTextContent("120");
//          type.appendChild(sequenceLength);
        }
        else if (e.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
          Element type = submission.createElementNS(null, "LS454");
          platform.appendChild(type);

          Element model = submission.createElementNS(null, "INSTRUMENT_MODEL");
          model.setTextContent(e.getPlatform().getInstrumentModel());
          type.appendChild(model);

//DEPRECATED SRA 1.5
          /*
          Element keySequence = submission.createElementNS(null, "KEY_SEQUENCE");
          if (relevantLibrary != null &&
              relevantLibrary.getLibraryType() != null &&
              !relevantLibrary.getLibraryType().getDescription().equals("")) {
            if (relevantLibrary.getLibraryType().getDescription().equals("Rapid Shotgun")) {
              keySequence.setTextContent("GATC");
            }
            else {
              keySequence.setTextContent("TCAG");
            }
          }
          type.appendChild(keySequence);

          Element flowSequence = submission.createElementNS(null, "FLOW_SEQUENCE");
          flowSequence.setTextContent("ATCG");
          type.appendChild(flowSequence);

          Element flowCount = submission.createElementNS(null, "FLOW_COUNT");
          //ls454 400bp (max 500?)
          flowCount.setTextContent("400");
          type.appendChild(flowCount);
          */
        }
        else if (e.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
          Element type = submission.createElementNS(null, "ABI_SOLID");
          platform.appendChild(type);

          Element model = submission.createElementNS(null, "INSTRUMENT_MODEL");
          model.setTextContent(e.getPlatform().getInstrumentModel());
          type.appendChild(model);
//DEPRECATED SRA 1.5
          /*
          Element colourMatrix = submission.createElementNS(null, "COLOR_MATRIX");
          Element colour = submission.createElementNS(null, "COLOR");
          colour.setAttribute("dibase", "");
          colourMatrix.appendChild(colour);
          type.appendChild(colourMatrix);

          Element colourMatrixCode = submission.createElementNS(null, "COLOR_MATRIX_CODE");
          type.appendChild(colourMatrixCode);

          Element sequenceLength = submission.createElementNS(null, "SEQUENCE_LENGTH");
          ////solid 40bp (max 50?)
          sequenceLength.setTextContent("40");
          type.appendChild(sequenceLength);
          */

//DEPRECATED SRA 1.2
//          Element cycleCount = submission.createElementNS(null, "CYCLE_COUNT");
//          cycleCount.setTextContent("40");
//          type.appendChild(cycleCount);
        }
        else {

        }

        experiment.appendChild(platform);
      }
      
      Element processing = submission.createElementNS(null, "PROCESSING");
      
      //DEPRECATED SRA 1.2
/*
      Element baseCalls = submission.createElementNS(null, "BASE_CALLS");
      processing.appendChild(baseCalls);
      Element sequenceSpace = submission.createElementNS(null, "SEQUENCE_SPACE");
      sequenceSpace.setTextContent("Base Space"); // Color space
      baseCalls.appendChild(sequenceSpace);
      Element baseCaller = submission.createElementNS(null, "BASE_CALLER");

      if (e.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
        baseCaller.setTextContent("Illumina RTA v1.8 primary analysis");
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
        baseCaller.setTextContent("454 GS Run Processor v2.3");
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
        baseCaller.setTextContent("SOLiD Analysis Tools primary analysis");
      }
      else {

      }
      baseCalls.appendChild(baseCaller);
*/
      
//DEPRECATED SRA 1.2
/*
      Element qualityScores = submission.createElementNS(null, "QUALITY_SCORES");
      qualityScores.setAttribute("qtype", "phred"); // other
      processing.appendChild(qualityScores);

      // these should be deprecated!
      Element numberOfLevels = submission.createElementNS(null, "NUMBER_OF_LEVELS");
      numberOfLevels.setTextContent("80");
      qualityScores.appendChild(numberOfLevels);
      Element multiplier = submission.createElementNS(null, "MULTIPLIER");
      multiplier.setTextContent("1.0");
      qualityScores.appendChild(multiplier);

      Element qualityScorer = submission.createElementNS(null, "QUALITY_SCORER");
      if (e.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
        qualityScorer.setTextContent("Illumina RTA v1.8 primary analysis");
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
        qualityScorer.setTextContent("454 GS Run Processor v2.3");
      }
      else if (e.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
        qualityScorer.setTextContent("SOLiD Analysis Tools primary analysis");
      }
      else {

      }
      qualityScores.appendChild(qualityScorer);
*/
      experiment.appendChild(processing);

      submission.getElementsByTagName("EXPERIMENT_SET").item(0).appendChild(experiment);
    }
  }
}
