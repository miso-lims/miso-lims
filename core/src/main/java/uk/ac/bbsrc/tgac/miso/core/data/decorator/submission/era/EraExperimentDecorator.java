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

import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Submittable;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.AbstractSubmittableDecorator;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

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

  @Override
  public void buildSubmission() {
    Experiment e = (Experiment) submittable;

    if (submission != null) {
      Element experiment = submission.createElementNS(null, "EXPERIMENT");
      experiment.setAttribute("alias", e.getAlias());
      experiment.setAttribute("center_name", submissionProperties.getProperty("submission.centreName"));

      Element experimentTitle = submission.createElementNS(null, "TITLE");
      experimentTitle.setTextContent(e.getTitle());
      experiment.appendChild(experimentTitle);

      Element studyRef = submission.createElementNS(null, "STUDY_REF");
      studyRef.setAttribute("refname", e.getStudy().getAlias());
      studyRef.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
      if (!isStringEmptyOrNull(e.getStudy().getAccession())) {
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
          // multiplexed pool
          Element pool = submission.createElementNS(null, "POOL");
          sampleDescriptor.appendChild(pool);

          for (Dilution dil : e.getPool().getDilutions()) {
            relevantLibrary = dil.getLibrary();
            Element member = submission.createElementNS(null, "MEMBER");
            member.setAttribute("member_name", dil.getName());
            member.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
            member.setAttribute("refname", relevantLibrary.getSample().getAlias());
            if (!isStringEmptyOrNull(relevantLibrary.getSample().getAccession())) {
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
        } else {
          for (Dilution dil : e.getPool().getDilutions()) {
            relevantLibrary = dil.getLibrary();
            sampleDescriptor.setAttribute("refname", relevantLibrary.getSample().getAlias());
            sampleDescriptor.setAttribute("refcenter", submissionProperties.getProperty("submission.centreName"));
            if (!isStringEmptyOrNull(relevantLibrary.getSample().getAccession())) {
              sampleDescriptor.setAttribute("accession", relevantLibrary.getSample().getAccession());
            }
          }
        }
      }

      design.appendChild(sampleDescriptor);

      Element libraryDescriptor = submission.createElementNS(null, "LIBRARY_DESCRIPTOR");
      Element libraryName = submission.createElementNS(null, "LIBRARY_NAME");
      if (relevantLibrary != null) {
        if (!isStringEmptyOrNull(e.getPool().getAlias())) {
          libraryName.setTextContent(e.getPool().getAlias());
        } else {
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
            layout.setAttribute("NOMINAL_LENGTH", String.valueOf(insert / qcs.size()));
          } else {
            layout.setAttribute("NOMINAL_LENGTH", "0");
          }
        } else {
          layout = submission.createElementNS(null, "SINGLE");
        }
        libraryLayout.appendChild(layout);
      }
      libraryDescriptor.appendChild(libraryLayout);

      Element poolingStrategy = submission.createElementNS(null, "POOLING_STRATEGY");
      if (e.getPool() != null) {
        if (e.getPool().getDilutions().size() > 1) {
          poolingStrategy.setTextContent("multiplexed libraries");
        } else {
          poolingStrategy.setTextContent("none");
        }
      }
      libraryDescriptor.appendChild(poolingStrategy);

      design.appendChild(libraryDescriptor);
      if (e.getPlatform() != null) {
        Element platform = submission.createElementNS(null, "PLATFORM");

        if (e.getPlatform().getPlatformType().equals(PlatformType.ILLUMINA)) {
          Element type = submission.createElementNS(null, "ILLUMINA");
          platform.appendChild(type);

          Element model = submission.createElementNS(null, "INSTRUMENT_MODEL");
          model.setTextContent(e.getPlatform().getInstrumentModel());
          type.appendChild(model);

        } else if (e.getPlatform().getPlatformType().equals(PlatformType.LS454)) {
          Element type = submission.createElementNS(null, "LS454");
          platform.appendChild(type);

          Element model = submission.createElementNS(null, "INSTRUMENT_MODEL");
          model.setTextContent(e.getPlatform().getInstrumentModel());
          type.appendChild(model);

        } else if (e.getPlatform().getPlatformType().equals(PlatformType.SOLID)) {
          Element type = submission.createElementNS(null, "ABI_SOLID");
          platform.appendChild(type);

          Element model = submission.createElementNS(null, "INSTRUMENT_MODEL");
          model.setTextContent(e.getPlatform().getInstrumentModel());
          type.appendChild(model);
        } else {

        }

        experiment.appendChild(platform);
      }

      Element processing = submission.createElementNS(null, "PROCESSING");

      experiment.appendChild(processing);

      submission.getElementsByTagName("EXPERIMENT_SET").item(0).appendChild(experiment);
    }
  }
}
