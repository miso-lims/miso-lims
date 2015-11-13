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

package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;
import java.util.Set;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import uk.ac.bbsrc.tgac.miso.core.data.type.SubmissionActionType;
import uk.ac.bbsrc.tgac.miso.core.exception.SubmissionException;
import uk.ac.bbsrc.tgac.miso.core.manager.SubmissionManager;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * Created by IntelliJ IDEA. User: davey Date: 10-Feb-2010 Time: 09:43:30
 */
@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
@JsonIgnoreProperties({ "securityProfile" })
public interface Submission<I, O, R> extends Submittable<O>, SecurableByProfile, Nameable, Comparable {

  public static final Long UNSAVED_ID = 0L;

  @Deprecated
  public Long getSubmissionId();

  @Deprecated
  public void setSubmissionId(Long submissionId);

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

  public void addSubmissionElement(I i);

  public Set<Submittable<O>> getSubmissionElements();

  public SubmissionActionType getSubmissionActionType();

  public void setSubmissionActionType(SubmissionActionType submissionActionType);

  public R submit(SubmissionManager<I, O, R> manager) throws SubmissionException;

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);
}
