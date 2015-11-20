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

package uk.ac.bbsrc.tgac.miso.core.data.impl;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import net.sourceforge.fluxion.spi.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.factory.submission.ERASubmissionFactory;

import uk.ac.bbsrc.tgac.miso.core.data.AbstractSample;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

/**
 * uk.ac.bbsrc.tgac.miso.core.data.impl
 * <p/>
 * TODO Info
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "Sample")
public class SampleImpl extends AbstractSample implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Construct a new Sample with a default empty SecurityProfile
   */
  public SampleImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new Sample with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public SampleImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  /**
   * If the given User can read the parent Project, construct a new Sample with a SecurityProfile inherited from the parent Project. If not,
   * construct a new Sample with a SecurityProfile owned by the given User
   * 
   * @param project
   *          of type Project
   * @param user
   *          of type User
   */
  public SampleImpl(Project project, User user) {
    if (project.userCanRead(user)) {
      setProject(project);
      setSecurityProfile(project.getSecurityProfile());
    } else {
      setSecurityProfile(new SecurityProfile(user));
    }
  }

  public SampleImpl(SampleFactoryBuilder builder) {
    this(builder.getUser());
    setProject(builder.getProject());
    setDescription(builder.getDescription());
    setSampleType(builder.getSampleType());
    setScientificName(builder.getScientificName());

    if (!LimsUtils.isStringEmptyOrNull(builder.getAccession())) {
      setAccession(builder.getAccession());
    }
    if (!LimsUtils.isStringEmptyOrNull(builder.getName())) {
      setName(builder.getName()); // Required, but will be set later.
    }
    if (!LimsUtils.isStringEmptyOrNull(builder.getIdentificationBarcode())) {
      setIdentificationBarcode(builder.getIdentificationBarcode());
    }
    if (!LimsUtils.isStringEmptyOrNull(builder.getLocationBarcode())) {
      setLocationBarcode(builder.getLocationBarcode());
    }
    if (builder.getReceivedDate() != null) {
      setReceivedDate(builder.getReceivedDate());
    }
    if (builder.getQcPassed() != null) {
      setQcPassed(builder.getQcPassed());
    }
    if (!LimsUtils.isStringEmptyOrNull(builder.getAlias())) {
      setAlias(builder.getAlias());
    }
    if (!LimsUtils.isStringEmptyOrNull(builder.getTaxonIdentifier())) {
      setTaxonIdentifier(builder.getTaxonIdentifier());
    }
  }

  @Override
  public void buildSubmission() {
    /*
     * try { DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); submissionDocument =
     * docBuilder.newDocument(); } catch (ParserConfigurationException e) { e.printStackTrace(); }
     * ERASubmissionFactory.generateSampleSubmissionXML(submissionDocument, this);
     */
  }

  /**
   * Method buildReport ...
   */
  @Override
  public void buildReport() {

  }
}
