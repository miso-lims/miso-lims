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
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.Submittable;
import uk.ac.bbsrc.tgac.miso.core.data.decorator.AbstractSubmittableDecorator;
import uk.ac.bbsrc.tgac.miso.core.util.TgacSubmissionConstants;

import java.util.Properties;

/**
 * Decorates a Sample so that an ERA submission XML document can be built from it
 *
 * @author Rob Davey
 * @date 12-Oct-2010
 * @since 0.0.2
 */
public class EraSampleDecorator extends AbstractSubmittableDecorator<Document> {

  public EraSampleDecorator(Submittable submittable, Properties submissionProperties, Document submission) {
    super(submittable, submissionProperties);
    this.submission = submission;
  }

  public void buildSubmission() {
    //submittable.buildSubmission();

    Sample sample = (Sample)submittable;
    Element s = submission.createElementNS(null, "SAMPLE");

    s.setAttribute("alias", sample.getAlias());

    s.setAttribute("center_name", submissionProperties.getProperty("submission.centreName"));

    Element sampleTitle = submission.createElementNS(null, "TITLE");
    sampleTitle.setTextContent(sample.getAlias());
    s.appendChild(sampleTitle);

    Element sampleName = submission.createElementNS(null, "SAMPLE_NAME");
    Element sampleScientificName = submission.createElementNS(null, "SCIENTIFIC_NAME");
    sampleScientificName.setTextContent(sample.getScientificName());
    sampleName.appendChild(sampleScientificName);


    //2/11/2011 Antony Colles moved IF !=null statement, to help produce valid submission XML.
    Element sampleTaxonIdentifier = submission.createElementNS(null, "TAXON_ID");
    if (sample.getTaxonIdentifier() != null && !sample.getTaxonIdentifier().equals(""))
    {
      sampleTaxonIdentifier.setTextContent(sample.getTaxonIdentifier());
    }
    else
    {
      sampleTaxonIdentifier.setTextContent("000001");
    }
    sampleName.appendChild(sampleTaxonIdentifier);

    s.appendChild(sampleName);

    Element sampleDescription = submission.createElementNS(null, "DESCRIPTION");
    sampleDescription.setTextContent(sample.getDescription());
    s.appendChild(sampleDescription);

    submission.getElementsByTagName("SAMPLE_SET").item(0).appendChild(s);
  }
}
