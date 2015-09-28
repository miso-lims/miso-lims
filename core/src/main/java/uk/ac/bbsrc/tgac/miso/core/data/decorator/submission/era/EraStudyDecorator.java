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
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submittable;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.AbstractSubmittableDecorator;

import java.util.Properties;

/**
 * Decorates a Study so that an ERA Study submission XML document can be built from it
 *
 * @author Rob Davey
 * @date 12-Oct-2010
 * @since 0.0.2
 */
public class EraStudyDecorator extends AbstractSubmittableDecorator<Document> {

  public EraStudyDecorator(Submittable submittable, Properties submissionProperties, Document submission) {
    super(submittable, submissionProperties);
    this.submission = submission;
  }

  public void buildSubmission() {
    Study s = (Study)submittable;
    if (submission != null) {
      Element study = submission.createElement("STUDY");
      //study.setAttribute("accession", s.getAccession());
      study.setAttribute("alias", s.getAlias());

      Element studyDescriptor = submission.createElementNS(null, "DESCRIPTOR");
      Element studyTitle = submission.createElementNS(null, "STUDY_TITLE");
      studyTitle.setTextContent(s.getAlias());
      studyDescriptor.appendChild(studyTitle);

      Element studyType = submission.createElementNS(null, "STUDY_TYPE");
      studyType.setAttribute("existing_study_type", s.getStudyType());
      studyDescriptor.appendChild(studyType);

      // DEPRECATED SRA 1.2
      //Element centerName = doc.createElementNS(null, "CENTER_NAME");
      //centerName.setTextContent(TgacSubmissionConstants.CENTRE_NAME.getKey());
      //studyDescriptor.appendChild(centerName);

      Element centerProjectName = submission.createElementNS(null, "CENTER_PROJECT_NAME");
      centerProjectName.setTextContent(s.getProject().getAlias());
      studyDescriptor.appendChild(centerProjectName);

      Element studyAbstract = submission.createElementNS(null, "STUDY_ABSTRACT");
      //TODO - add Study.getAbstract()
      studyAbstract.setTextContent(s.getAbstract());
      studyDescriptor.appendChild(studyAbstract);

      Element studyDescription = submission.createElementNS(null, "STUDY_DESCRIPTION");
      studyDescription.setTextContent(s.getDescription());
      studyDescriptor.appendChild(studyDescription);

      study.appendChild(studyDescriptor);

      submission.getElementsByTagName("STUDY_SET").item(0).appendChild(study);
    }
  }
}
