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

import uk.ac.bbsrc.tgac.miso.core.data.AbstractStudy;
import uk.ac.bbsrc.tgac.miso.core.data.Project;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

/**
 * Concrete implementation of a Study
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
public class StudyImpl extends AbstractStudy implements Serializable {

  /**
   * Construct a new Study with a default empty SecurityProfile
   */
  public StudyImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new Study with a SecurityProfile owned by the given User
   * 
   * @param user
   *          of type User
   */
  public StudyImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  /**
   * If the given User can read the parent Project, construct a new Study with a SecurityProfile inherited from the parent Project. If not,
   * construct a new Study with a SecurityProfile owned by the given User
   * 
   * @param project
   *          of type Project
   * @param user
   *          of type User
   */
  public StudyImpl(Project project, User user) {
    if (project.userCanRead(user)) {
      setProject(project);
      setSecurityProfile(project.getSecurityProfile());
    } else {
      setSecurityProfile(new SecurityProfile(user));
    }
  }

  public void buildSubmission() {
    /*
     * try { DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder(); submissionDocument =
     * docBuilder.newDocument(); } catch (ParserConfigurationException e) { e.printStackTrace(); }
     * ERASubmissionFactory.generateStudySubmissionXML(submissionDocument, this);
     */
  }
}