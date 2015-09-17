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
import java.util.Collections;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.w3c.dom.Document;

import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.PlatformImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;
import uk.ac.bbsrc.tgac.miso.core.util.CoverageIgnore;

/**
 * Skeleton implementation of an Experiment
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public abstract class AbstractExperiment implements Experiment {
   public static final Long UNSAVED_ID = 0L;

   @OneToOne(cascade = CascadeType.ALL)
   private SecurityProfile securityProfile;

   @Transient
   public Document submissionDocument;

   @ManyToOne(targetEntity = AbstractStudy.class, cascade = CascadeType.ALL)
   private Study study = null;

   // defines a pool on which this experiment will operate. This contains one or more dilutions of a sample
   private Pool pool;

   // defines the parent run which processes this experiment
   private Run run;

   private String title;
   private String name;
   private String description;
   private String alias;

   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private long experimentId = AbstractExperiment.UNSAVED_ID;
   private String accession;

   @OneToOne(targetEntity = PlatformImpl.class, cascade = CascadeType.ALL)
   private Platform platform;

   private Collection<Kit> kits = new HashSet<Kit>();

   @Override
   public Study getStudy() {
      return study;
   }

   @Override
   public void setStudy(Study study) {
      this.study = study;
   }

   @CoverageIgnore
   @Override
   @Deprecated
   public Long getExperimentId() {
      return experimentId;
   }

   @CoverageIgnore
   @Override
   @Deprecated
   public void setExperimentId(Long experimentId) {
      this.experimentId = experimentId;
   }

   @Override
   public long getId() {
      return experimentId;
   }

   @Override
   public void setId(long id) {
      this.experimentId = id;
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
   public String getTitle() {
      return title;
   }

   @Override
   public void setTitle(String title) {
      this.title = title;
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
   public Platform getPlatform() {
      return platform;
   }

   @Override
   public void setPlatform(Platform platform) {
      this.platform = platform;
   }

   /*
    * public void addRun(Run r) throws MalformedRunException { try { //do experiment validation r.addExperiment(this);
    *
    * //propagate security profiles down the hierarchy r.setSecurityProfile(this.securityProfile);
    *
    * //add this.runs.add(r); } catch (MalformedExperimentException e) { e.printStackTrace(); } }
    *
    * public Collection<Run> getRuns() { return runs; }
    */

   @Override
   public Run getRun() {
      return run;
   }

   @Override
   public void setRun(Run run) {
      this.run = run;
   }

   /*
    * public void addSample(Sample s) throws MalformedSampleException { //do experiment validation try { s.addExperiment(this);
    *
    * //propagate security profiles down the hierarchy s.setSecurityProfile(this.securityProfile);
    *
    * //add this.samples.add(s); } catch (MalformedExperimentException e) { e.printStackTrace(); } }
    *
    * public Collection<Sample> getSamples() { return samples; }
    */

   @Override
   public Pool getPool() {
      return pool;
   }

   @Override
   public void setPool(Pool pool) {
      this.pool = pool;
   }

   @Override
   public Collection<Kit> getKits() {
      return kits;
   }

   @Override
   public Collection<Kit> getKitsByKitType(KitType kitType) {
      final ArrayList<Kit> ks = new ArrayList<Kit>();
      for (final Kit k : kits) {
         if (k.getKitDescriptor().getKitType().equals(kitType)) {
            ks.add(k);
         }
      }
      Collections.sort(ks);
      return ks;
   }

   @Override
   public void setKits(Collection<Kit> kits) {
      this.kits = kits;
   }

   @CoverageIgnore
   @Override
   public void addKit(Kit kit) {
      this.kits.add(kit);
   }

   /*
    * public Document getSubmissionData() { return submissionDocument; }
    *
    * public void accept(SubmittableVisitor v) { v.visit(this); }
    */

   @CoverageIgnore
   @Override
   public boolean isDeletable() {
      return getId() != AbstractExperiment.UNSAVED_ID;
      /*
       * && getKits().isEmpty() && getPool() == null;
       */
   }

   @Override
   public SecurityProfile getSecurityProfile() {
      return securityProfile;
   }

   @Override
   public void setSecurityProfile(SecurityProfile profile) {
      this.securityProfile = profile;
   }

   @Override
   public void inheritPermissions(SecurableByProfile parent) throws SecurityException {
      if (parent.getSecurityProfile().getOwner() != null) {
         setSecurityProfile(parent.getSecurityProfile());
      } else {
         throw new SecurityException("Cannot inherit permissions when parent object owner is not set!");
      }
   }

   @CoverageIgnore
   @Override
   public boolean userCanRead(User user) {
      return securityProfile.userCanRead(user);
   }

   @CoverageIgnore
   @Override
   public boolean userCanWrite(User user) {
      return securityProfile.userCanWrite(user);
   }

   @Override
   public abstract void buildSubmission();

   /**
    * Equivalency is based on getProjectId() if set, otherwise on name, description and creation date.
    */
   @CoverageIgnore
   @Override
   public boolean equals(Object obj) {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof Experiment)) return false;
      final Experiment them = (Experiment) obj;
      // If not saved, then compare resolved actual objects. Otherwise
      // just compare IDs.
      if (getId() == AbstractExperiment.UNSAVED_ID || them.getId() == AbstractExperiment.UNSAVED_ID) {
         if (getName() != null && them.getName() != null) {
            return getName().equals(them.getName());
         } else {
            return getAlias().equals(them.getAlias());
         }
      } else {
         return getId() == them.getId();
      }
   }

   @CoverageIgnore
   @Override
   public int hashCode() {
      if (getId() != AbstractExperiment.UNSAVED_ID) {
         return (int) getId();
      } else {
         final int PRIME = 37;
         int hashcode = 1;
         if (getName() != null) hashcode = PRIME * hashcode + getName().hashCode();
         if (getAlias() != null) hashcode = 37 * hashcode + getAlias().hashCode();
         return hashcode;
      }
   }

   @CoverageIgnore
   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append(getAccession());
      sb.append(" : ");
      sb.append(getTitle());
      sb.append(" : ");
      sb.append(getName());
      sb.append(" : ");
      sb.append(getDescription());
      sb.append(" : ");
      sb.append(getPool());
      sb.append(" : ");
      if (getPlatform() != null) {
         sb.append(getPlatform().getInstrumentModel());
         sb.append(" : ");
      }

      return sb.toString();
   }

   @CoverageIgnore
   @Override
   public int compareTo(Object o) {
      final Experiment t = (Experiment) o;
      if (getId() < t.getId()) return -1;
      if (getId() > t.getId()) return 1;
      return 0;
   }
}
