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

import javax.persistence.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.bbsrc.tgac.miso.core.data.AbstractProject;
import uk.ac.bbsrc.tgac.miso.core.factory.submission.ERASubmissionFactory;

/**
 * Concrete implementation of a Project, inheriting from the simlims core Project
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
public class ProjectImpl extends AbstractProject {
  /**
   * Construct a new Project with a default empty SecurityProfile
   */
  public ProjectImpl() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new Project with a SecurityProfile owned by the given User
   *
   * @param user of type User
   */
  public ProjectImpl(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }

  public void buildSubmission() {
    /*
    try {
      DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      submissionDocument = docBuilder.newDocument();
    }
    catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
    ERASubmissionFactory.generateProjectSubmissionXML(submissionDocument, this);
    */
  }

  /**
   * Method buildReport ...
   */
  public void buildReport() {

  }
}
