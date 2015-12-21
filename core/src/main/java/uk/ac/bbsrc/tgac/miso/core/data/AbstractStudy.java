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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.w3c.dom.Document;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.exception.MalformedExperimentException;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;


/**
 * Skeleton implementation of a Study
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
@Entity
@Table(name = "`Study`")
public abstract class AbstractStudy implements Study {
  public static final Long UNSAVED_ID = 0L;

  private static final long serialVersionUID = 1L;

  @ManyToOne(cascade = CascadeType.ALL)
  private Project project = null;

  @OneToMany(targetEntity = AbstractExperiment.class, cascade = CascadeType.ALL)
  private Collection<Experiment> experiments = new HashSet<Experiment>();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long studyId = AbstractStudy.UNSAVED_ID;

  @Transient
  public Document submissionDocument;

  @OneToOne(cascade = CascadeType.ALL)
  private SecurityProfile securityProfile = null;

  @Column(name = "name", nullable = false)
  private String name;
  @Column(name = "description", nullable = false)
  private String description;
  @Column(name = "accession")
  private String accession;
  @Column(name = "abstract")
  private String abs;
  @Column(name = "studyType")
  private String studyType;
  @Column(name = "alias")
  private String alias;
  private final Collection<ChangeLog> changeLog = new ArrayList<ChangeLog>();
  private User lastModifier;

  @Override
  public User getLastModifier() {
    return lastModifier;
  }

  @Override
  public void setLastModifier(User lastModifier) {
    this.lastModifier = lastModifier;
  }

  @Override
  public Collection<ChangeLog> getChangeLog() {
    return changeLog;
  }

  @Override
  public Project getProject() {
    return project;
  }

  @Override
  public void setProject(Project project) {
    this.project = project;
  }

  @Override
  @Deprecated
  public Long getStudyId() {
    return studyId;
  }

  @Override
  @Deprecated
  public void setStudyId(Long studyId) {
    this.studyId = studyId;
  }

  @Override
  public long getId() {
    return studyId;
  }

  @Override
  public void setId(long id) {
    this.studyId = id;
  }

  @Override
  public String getAccession() {
    return accession;
  }

  @Override
  public void setAccession(String accession) {
    this.accession = accession;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getAlias() {
    return alias;
  }

  @Override
  public void setAlias(String alias) {
    this.alias = alias;
  }

  @Override
  public String getAbstract() {
    return abs;
  }

  @Override
  public void setAbstract(String abs) {
    this.abs = abs;
  }

  @Override
  public String getStudyType() {
    return studyType;
  }

  @Override
  public void setStudyType(String studyType) {
    this.studyType = studyType;
  }

  @Override
  public void addExperiment(Experiment e) throws MalformedExperimentException {
    // do experiment validation

    // propagate security profiles down the hierarchy
    e.setSecurityProfile(this.securityProfile);

    // add
    this.experiments.add(e);
  }

  @Override
  public Collection<Experiment> getExperiments() {
    return experiments;
  }

  @Override
  public void setExperiments(Collection<Experiment> experiments) {
    this.experiments = experiments;
  }

  @Override
  public boolean isDeletable() {
    return getId() != AbstractStudy.UNSAVED_ID && getExperiments().isEmpty();
  }

  @Override
  public abstract void buildSubmission();

  @Override
  public SecurityProfile getSecurityProfile() {
    return securityProfile;
  }

  @Override
  public void setSecurityProfile(SecurityProfile securityProfile) {
    this.securityProfile = securityProfile;
  }

  @Override
  public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
    if (parent.getSecurityProfile().getOwner() != null) {
      setSecurityProfile(parent.getSecurityProfile());
    } else {
      throw new SecurityException("Cannot inherit permissions when parent object owner is not set!");
    }
  }

  @Override
  public boolean userCanRead(User user) {
    return securityProfile.userCanRead(user);
  }

  @Override
  public boolean userCanWrite(User user) {
    return securityProfile.userCanWrite(user);
  }

  /**
   * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (!(obj instanceof Study)) return false;
    Study them = (Study) obj;
    // If not saved, then compare resolved actual objects. Otherwise
    // just compare IDs.
    if (getId() == AbstractStudy.UNSAVED_ID || them.getId() == AbstractStudy.UNSAVED_ID) {
      if (getName() != null && them.getName() != null) {
        return getName().equals(them.getName());
      } else {
        return getAlias().equals(them.getAlias());
      }
    } else {
      return this.getId() == them.getId();
    }
  }

  @Override
  public int hashCode() {
    if (this.getId() != AbstractStudy.UNSAVED_ID) {
      return (int) getId();
    } else {
      final int PRIME = 37;
      int hashcode = 1;
      if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
      if (getAlias() != null) hashcode = PRIME * hashcode + getAlias().hashCode();
      return hashcode;
    }
  }

  @Override
  public int compareTo(Object o) {
    Study t = (Study) o;
    if (getId() < t.getId()) return -1;
    if (getId() > t.getId()) return 1;
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(getName());
    sb.append(" : ");
    sb.append(getAlias());
    sb.append(" : ");
    sb.append(getDescription());
    sb.append(" : ");
    sb.append(getStudyType());
    sb.append(" : ");

    if (getProject() != null) {
      sb.append(getProject().getAlias());
      sb.append("(" + getProject().getName() + ")");
    }
    return sb.toString();
  }
}
