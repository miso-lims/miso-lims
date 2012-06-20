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

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.w3c.dom.Document;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.visitor.SubmittableVisitor;
import uk.ac.bbsrc.tgac.miso.core.factory.submission.ERASubmissionFactory;

import javax.persistence.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Concrete implementation of a Partition to represent a Lane in a sequencing platform that uses lanes, e.g.
 *  Illumina
 *
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "`Lane`")
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonIgnoreProperties({"securityProfile","flowcell"})
@Deprecated
public class Lane extends PartitionImpl {
  public static final Long UNSAVED_ID = null;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public Long laneId = Lane.UNSAVED_ID;

  /**
   * Construct a new Lane with a default empty SecurityProfile
   */
  public Lane() {
    setSecurityProfile(new SecurityProfile());
  }

  /**
   * Construct a new Lane with a SecurityProfile owned by the given User
   *
   * @param user of type User
   */
  public Lane(User user) {
    setSecurityProfile(new SecurityProfile(user));
  }
}