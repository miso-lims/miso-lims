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

package uk.ac.bbsrc.tgac.miso.core.data.impl.pacbio;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.RunImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl.ls454
 * <p/>
 * TODO Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@DiscriminatorValue("PACBIO")
public class PacBioRun extends RunImpl {

  public PacBioRun() {
    setPlatformType(PlatformType.PACBIO);
    setStatus(new PacBioStatus());
  }

  public PacBioRun(String statusXml) {
    this(statusXml, null);
  }

  public PacBioRun(User user) {
    setPlatformType(PlatformType.PACBIO);
    setStatus(new PacBioStatus());
    setSecurityProfile(new SecurityProfile(user));
  }

  public PacBioRun(String statusXml, User user) {
    setPlatformType(PlatformType.PACBIO);
    setStatus(new PacBioStatus(statusXml));
    setSecurityProfile(new SecurityProfile(user));
  }

  @Override
  public void buildSubmission() {
    /*
     * try { DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); submissionDocument =
     * docBuilder.newDocument(); } catch (ParserConfigurationException e) { e.printStackTrace(); }
     * ERASubmissionFactory.generateFullRunSubmissionXML(submissionDocument, this);
     */
  }

  /**
   * Method buildReport ...
   */
  @Override
  public void buildReport() {

  }
}
