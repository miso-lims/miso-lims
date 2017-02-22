/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey @ TGAC
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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;

/**
 * Created by IntelliJ IDEA. User: davey Date: 10-Feb-2010 Time: 09:43:30
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile" })
public interface Submission extends Nameable, Comparable<Submission> {

  public static final Long UNSAVED_ID = 0L;

  public void setId(long id);

  public void setName(String name);

  public String getAlias();

  public void setAlias(String alias);

  public String getAccession();

  public void setAccession(String accession);

  public String getDescription();

  public void setDescription(String description);

  public String getTitle();

  public void setTitle(String title);

  public Date getCreationDate();

  public void setCreationDate(Date creationDate);

  public Date getSubmissionDate();

  public void setSubmissionDate(Date submissionDate);

  public boolean isVerified();

  public void setVerified(boolean verified);

  public boolean isCompleted();

  public void setCompleted(boolean completed);

  public SubmissionActionType getSubmissionActionType();

  public void setSubmissionActionType(SubmissionActionType submissionActionType);

  Set<Sample> getSamples();

  void setSamples(Set<Sample> samples);

  Set<Study> getStudies();

  void setStudies(Set<Study> studies);

  Set<Experiment> getExperiments();

  void setExperiments(Set<Experiment> experiments);

  Map<Dilution, Partition> getDilutions();

  void setDilutions(Map<Dilution, Partition> dilutions);
}
