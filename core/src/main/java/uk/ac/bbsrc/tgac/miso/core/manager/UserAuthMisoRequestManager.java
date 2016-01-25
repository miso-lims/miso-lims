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

package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Boxable;
import uk.ac.bbsrc.tgac.miso.core.data.ChangeLog;
import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Kit;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Plateable;
import uk.ac.bbsrc.tgac.miso.core.data.Platform;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.PoolQC;
import uk.ac.bbsrc.tgac.miso.core.data.Poolable;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunQC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPoolPartition;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Status;
import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.TagBarcode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.KitType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.security.SecurableByProfile;

/**
 * uk.ac.bbsrc.tgac.miso.core.manager
 * <p/>
 * Info
 * 
 * @author Rob Davey
 * @date 22-Aug-2011
 * @since 0.1.0
 */
public class UserAuthMisoRequestManager implements RequestManager {
  protected static final Logger log = LoggerFactory.getLogger(UserAuthMisoRequestManager.class);

  private SecurityContextHolderStrategy securityContextHolderStrategy;
  private SecurityManager securityManager;
  @Autowired
  private RequestManager backingManager;

  public RequestManager getBackingManager() {
    return backingManager;
  }

  public void setBackingManager(RequestManager backingManager) {
    this.backingManager = backingManager;
  }

  public UserAuthMisoRequestManager() {
  }

  public UserAuthMisoRequestManager(SecurityContextHolderStrategy securityContextHolderStrategy, SecurityManager securityManager) {
    this.securityContextHolderStrategy = securityContextHolderStrategy;
    this.securityManager = securityManager;
  }

  private User getCurrentUser() throws IOException {
    Authentication auth = securityContextHolderStrategy.getContext().getAuthentication();
    User user = securityManager.getUserByLoginName(securityContextHolderStrategy.getContext().getAuthentication().getName());
    if (user == null && auth.isAuthenticated()) {
      user = new UserImpl();
      user.setAdmin(true);
      user.setActive(true);
      return user;
    }
    return securityManager.getUserByLoginName(securityContextHolderStrategy.getContext().getAuthentication().getName());
  }

  private boolean readCheck(SecurableByProfile s) throws IOException {
    if (s != null) {
      try {
        return s.userCanRead(getCurrentUser());
      } catch (IOException e) {
        log.error("Cannot resolve a currently logged in user", e);
      }
    } else {
      return true;
    }
    return false;
  }

  private boolean writeCheck(SecurableByProfile s) throws IOException {
    if (s != null) {
      try {
        return s.userCanWrite(getCurrentUser());
      } catch (IOException e) {
        log.error("cannot resolve a currently logged in user", e);
      }
    } else {
      throw new IOException("Cannot check write permissions for null object. Does this object really exist?");
    }
    return false;
  }

  @Override
  public long saveProject(Project project) throws IOException {
    if (writeCheck(project)) {
      return backingManager.saveProject(project);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Project");
    }
  }

  @Override
  public long saveProjectOverview(ProjectOverview overview) throws IOException {
    if (writeCheck(overview.getProject())) {
      return backingManager.saveProjectOverview(overview);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to the parent Project");
    }
  }

  @Override
  public long saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    if (writeCheck(overview.getProject())) {
      return backingManager.saveProjectOverviewNote(overview, note);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to the parent Project");
    }
  }

  @Override
  public long saveRun(Run run) throws IOException {
    if (writeCheck(run)) {
      return backingManager.saveRun(run);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Run");
    }
  }

  @Override
  public long saveRunQC(RunQC runQC) throws IOException {
    if (writeCheck(runQC.getRun())) {
      return backingManager.saveRunQC(runQC);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to the parent Run");
    }
  }

  @Override
  public long saveSample(Sample sample) throws IOException {
    if (writeCheck(sample)) {
      return backingManager.saveSample(sample);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Sample");
    }
  }

  @Override
  public long saveSampleQC(SampleQC sampleQC) throws IOException {
    if (writeCheck(sampleQC.getSample())) {
      return backingManager.saveSampleQC(sampleQC);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to the parent Sample ");
    }
  }

  @Override
  public long saveSampleNote(Sample sample, Note note) throws IOException {
    if (writeCheck(sample)) {
      return backingManager.saveSampleNote(sample, note);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Sample");
    }
  }

  @Override
  public long saveLibrary(Library library) throws IOException {
    if (writeCheck(library)) {
      return backingManager.saveLibrary(library);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Library");
    }
  }

  @Override
  public long saveLibraryDilution(LibraryDilution libraryDilution) throws IOException {
    if (writeCheck(libraryDilution)) {
      return backingManager.saveLibraryDilution(libraryDilution);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this LibraryDilution");
    }
  }

  @Override
  public long saveLibraryNote(Library library, Note note) throws IOException {
    if (writeCheck(library)) {
      return backingManager.saveLibraryNote(library, note);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Library");
    }
  }

  @Override
  public long saveLibraryQC(LibraryQC libraryQC) throws IOException {
    if (writeCheck(libraryQC.getLibrary())) {
      return backingManager.saveLibraryQC(libraryQC);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Library");
    }
  }

  @Override
  public long savePool(Pool pool) throws IOException {
    if (writeCheck(pool)) {
      return backingManager.savePool(pool);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Pool");
    }
  }

  @Override
  public long savePoolQC(PoolQC poolQC) throws IOException {
    if (writeCheck(poolQC.getPool())) {
      return backingManager.savePoolQC(poolQC);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Pool");
    }
  }

  @Override
  public long saveEmPCR(emPCR pcr) throws IOException {
    if (writeCheck(pcr)) {
      return backingManager.saveEmPCR(pcr);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this EmPCR");
    }
  }

  @Override
  public long saveEmPCRDilution(emPCRDilution dilution) throws IOException {
    if (writeCheck(dilution)) {
      return backingManager.saveEmPCRDilution(dilution);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this EmPCRDilution");
    }
  }

  @Override
  public long saveExperiment(Experiment experiment) throws IOException {
    if (writeCheck(experiment)) {
      return backingManager.saveExperiment(experiment);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Experiment");
    }
  }

  @Override
  public long saveStudy(Study study) throws IOException {
    if (writeCheck(study)) {
      return backingManager.saveStudy(study);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Study");
    }
  }

  @Override
  public long saveSequencerPoolPartition(SequencerPoolPartition partition) throws IOException {
    if (writeCheck(partition)) {
      return backingManager.saveSequencerPoolPartition(partition);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Partition");
    }
  }

  @Override
  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException {
    if (writeCheck(container)) {
      return backingManager.saveSequencerPartitionContainer(container);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this SequencerPartitionContainer");
    }
  }

  @Override
  public long saveSubmission(Submission submission) throws IOException {
    if (writeCheck(submission)) {
      return backingManager.saveSubmission(submission);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Submission");
    }
  }

  @Override
  public long saveEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
    return backingManager.saveEntityGroup(entityGroup);
  }

  // gets
  @Override
  public SequencerPoolPartition getSequencerPoolPartitionById(long partitionId) throws IOException {
    SequencerPoolPartition o = backingManager.getSequencerPoolPartitionById(partitionId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Partition " + partitionId);
  }

  @Override
  public Experiment getExperimentById(long experimentId) throws IOException {
    Experiment o = backingManager.getExperimentById(experimentId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Experiment " + experimentId);
  }

  @Override
  public Pool<? extends Poolable> getPoolById(long poolId) throws IOException {
    Pool<? extends Poolable> o = backingManager.getPoolById(poolId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Pool " + poolId);
  }

  @Override
  public Pool<? extends Poolable> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException {
    Pool<? extends Poolable> o = backingManager.getPoolByBarcode(barcode, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Pool " + o.getId());
  }

  @Override
  public Pool<? extends Poolable> getPoolByIdBarcode(String barcode) throws IOException {
    Pool<? extends Poolable> o = backingManager.getPoolByIdBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Pool " + o.getId());
  }

  @Override
  public Pool<? extends Poolable> getPoolByBarcode(String barcode) throws IOException {
    Pool<? extends Poolable> o = backingManager.getPoolByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Pool " + o.getId());
  }

  @Override
  public PoolQC getPoolQCById(long qcId) throws IOException {
    PoolQC o = backingManager.getPoolQCById(qcId);
    if (readCheck(o.getPool()))
      return o;
    else
      throw new IOException(
          "User " + getCurrentUser().getFullName() + " cannot read parent Pool " + o.getPool().getId() + " for PoolQC " + qcId);
  }

  @Override
  public Library getLibraryById(long libraryId) throws IOException {
    Library o = backingManager.getLibraryById(libraryId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Library " + libraryId);
  }

  @Override
  public Library getLibraryByBarcode(String barcode) throws IOException {
    Library o = backingManager.getLibraryByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Library " + o.getId());
  }

  @Override
  public Library getLibraryByAlias(String alias) throws IOException {
    Library o = backingManager.getLibraryByAlias(alias);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Library " + o.getId());
  }

  @Override
  public Dilution getDilutionByBarcode(String barcode) throws IOException {
    Dilution o = backingManager.getDilutionByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Dilution " + o.getId());
  }

  @Override
  public Dilution getDilutionByIdAndPlatform(long dilutionid, PlatformType platformType) throws IOException {
    Dilution o = backingManager.getDilutionByIdAndPlatform(dilutionid, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Dilution " + o.getId());
  }

  @Override
  public Dilution getDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    Dilution o = backingManager.getDilutionByBarcodeAndPlatform(barcode, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Dilution " + o.getId());
  }

  @Override
  public LibraryDilution getLibraryDilutionById(long dilutionId) throws IOException {
    LibraryDilution o = backingManager.getLibraryDilutionById(dilutionId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read LibraryDilution " + dilutionId);
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException {
    LibraryDilution o = backingManager.getLibraryDilutionByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read LibraryDilution " + o.getId());
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    LibraryDilution o = backingManager.getLibraryDilutionByBarcodeAndPlatform(barcode, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read LibraryDilution " + o.getId());
  }

  @Override
  public LibraryQC getLibraryQCById(long qcId) throws IOException {
    LibraryQC o = backingManager.getLibraryQCById(qcId);
    if (readCheck(o.getLibrary()))
      return o;
    else
      throw new IOException(
          "User " + getCurrentUser().getFullName() + " cannot read parent Library " + o.getLibrary().getId() + " for LibraryQC " + qcId);
  }

  @Override
  public emPCR getEmPCRById(long pcrId) throws IOException {
    emPCR o = backingManager.getEmPCRById(pcrId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read emPCR " + pcrId);
  }

  @Override
  public emPCRDilution getEmPCRDilutionById(long dilutionId) throws IOException {
    emPCRDilution o = backingManager.getEmPCRDilutionById(dilutionId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read emPCRDilution " + dilutionId);
  }

  @Override
  public emPCRDilution getEmPCRDilutionByBarcode(String barcode) throws IOException {
    emPCRDilution o = backingManager.getEmPCRDilutionByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read emPCRDilution " + o.getId());
  }

  @Override
  public emPCRDilution getEmPCRDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    emPCRDilution o = backingManager.getEmPCRDilutionByBarcodeAndPlatform(barcode, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read emPCRDilution " + o.getId());
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerById(long containerId) throws IOException {
    SequencerPartitionContainer o = backingManager.getSequencerPartitionContainerById(containerId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read SequencerPartitionContainer " + containerId);
  }

  @Override
  public Note getNoteById(long noteId) throws IOException {
    Note o = backingManager.getNoteById(noteId);
    User user = getCurrentUser();
    if (o.getOwner().equals(user) || user.isAdmin() || (o.isInternalOnly() && user.isInternal()))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Note " + o.getNoteId());
  }

  @Override
  public Project getProjectById(long projectId) throws IOException {
    Project o = backingManager.getProjectById(projectId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Project " + projectId);
  }

  @Override
  public Project getProjectByAlias(String projectAlias) throws IOException {
    Project o = backingManager.getProjectByAlias(projectAlias);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Project " + projectAlias);
  }

  @Override
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    ProjectOverview o = backingManager.getProjectOverviewById(overviewId);
    if (readCheck(o.getProject()))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read parent Project " + o.getProject().getProjectId()
          + " for ProjectOverview " + overviewId);
  }

  @Override
  public Run getRunById(long runId) throws IOException {
    Run o = backingManager.getRunById(runId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Run " + runId);
  }

  @Override
  public Run getRunByAlias(String alias) throws IOException {
    Run o = backingManager.getRunByAlias(alias);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Run " + o.getId());
  }

  @Override
  public RunQC getRunQCById(long runQcId) throws IOException {
    RunQC o = backingManager.getRunQCById(runQcId);
    if (readCheck(o.getRun()))
      return o;
    else
      throw new IOException(
          "User " + getCurrentUser().getFullName() + " cannot read parent Run " + o.getRun().getId() + " for RunQC " + runQcId);
  }

  @Override
  public Sample getSampleById(long sampleId) throws IOException {
    Sample o = backingManager.getSampleById(sampleId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Sample " + sampleId);
  }

  @Override
  public Sample getSampleByBarcode(String barcode) throws IOException {
    Sample o = backingManager.getSampleByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Sample " + o.getId());
  }

  @Override
  public SampleQC getSampleQCById(long sampleQcId) throws IOException {
    SampleQC o = backingManager.getSampleQCById(sampleQcId);
    if (readCheck(o.getSample()))
      return o;
    else
      throw new IOException(
          "User " + getCurrentUser().getFullName() + " cannot read parent Run " + o.getSample().getId() + " for SampleQC " + sampleQcId);
  }

  @Override
  public Status getStatusByRunName(String runName) throws IOException {
    Run o = backingManager.getRunByAlias(runName);
    if (readCheck(o))
      return backingManager.getStatusByRunName(runName);
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read parent Run " + o.getId() + " for Status");
  }

  @Override
  public Study getStudyById(long studyId) throws IOException {
    Study o = backingManager.getStudyById(studyId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Study " + studyId);
  }

  @Override
  public Submission getSubmissionById(long submissionId) throws IOException {
    Submission o = backingManager.getSubmissionById(submissionId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Submission " + submissionId);
  }

  @Override
  public Plate<? extends List<? extends Plateable>, ? extends Plateable> getPlateById(long plateId) throws IOException {
    Plate<? extends List<? extends Plateable>, ? extends Plateable> p = backingManager.getPlateById(plateId);
    if (readCheck(p))
      return p;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Plate " + plateId);
  }

  @Override
  public <T extends List<S>, S extends Plateable> Plate<T, S> getPlateByBarcode(String barcode) throws IOException {
    Plate<T, S> p = backingManager.<T, S> getPlateByBarcode(barcode);
    if (readCheck(p))
      return p;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Plate " + p.getId());
  }

  @Override
  public EntityGroup<? extends Nameable, ? extends Nameable> getEntityGroupById(long entityGroupId) throws IOException {
    return backingManager.getEntityGroupById(entityGroupId);
  }

  /* lists */

  @Override
  public Collection<Project> listAllProjects() throws IOException {
    User user = getCurrentUser();
    Collection<Project> accessibles = new HashSet<>();
    for (Project project : backingManager.listAllProjects()) {
      if (project.userCanRead(user)) {
        accessibles.add(project);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<Project> accessibles = new HashSet<>();
    for (Project project : backingManager.listAllProjectsWithLimit(limit)) {
      if (project.userCanRead(user)) {
        accessibles.add(project);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Project> listAllProjectsBySearch(String query) throws IOException {
    User user = getCurrentUser();
    Collection<Project> accessibles = new HashSet<>();
    for (Project project : backingManager.listAllProjectsBySearch(query)) {
      if (project.userCanRead(user)) {
        accessibles.add(project);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<ProjectOverview> listAllOverviewsByProjectId(long projectId) throws IOException {
    User user = getCurrentUser();
    Collection<ProjectOverview> accessibles = new HashSet<>();
    for (ProjectOverview projectOverview : backingManager.listAllOverviewsByProjectId(projectId)) {
      if (projectOverview.getProject().userCanRead(user)) {
        accessibles.add(projectOverview);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Run> listAllRuns() throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    Collection<Run> runs = backingManager.listAllRuns();
    if (runs != null) {
      for (Run run : backingManager.listAllRuns()) {
        if (run != null) {
          if (run.userCanRead(user)) {
            accessibles.add(run);
          }
        } else {
          log.error("WTF. Seems to be a null run in the cached list");
        }
      }
    } else {
      log.error("WTF. Run list coming from cache is null");
    }
    return accessibles;
  }

  @Override
  public Collection<Run> listAllRunsWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    Collection<Run> runs = backingManager.listAllRunsWithLimit(limit);
    if (runs != null) {
      for (Run run : backingManager.listAllRunsWithLimit(limit)) {
        if (run != null) {
          if (run.userCanRead(user)) {
            accessibles.add(run);
          }
        } else {
          log.error("WTF. Seems to be a null run in the cached list");
        }
      }
    } else {
      log.error("WTF. Run list coming from cache is null");
    }
    return accessibles;
  }

  @Override
  public Collection<Run> listAllRunsBySearch(String query) throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    for (Run run : backingManager.listAllRunsBySearch(query)) {
      if (run.userCanRead(user)) {
        accessibles.add(run);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Run> listAllRunsByProjectId(long projectId) throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    for (Run run : backingManager.listAllRunsByProjectId(projectId)) {
      if (run.userCanRead(user)) {
        accessibles.add(run);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Run> listRunsByPoolId(long poolId) throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    for (Run run : backingManager.listRunsByPoolId(poolId)) {
      if (run.userCanRead(user)) {
        accessibles.add(run);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Run> listRunsBySequencerPartitionContainerId(long containerId) throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    for (Run run : backingManager.listRunsBySequencerPartitionContainerId(containerId)) {
      if (run.userCanRead(user)) {
        accessibles.add(run);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Run> listAllLS454Runs() throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    for (Run run : backingManager.listAllLS454Runs()) {
      if (run.userCanRead(user)) {
        accessibles.add(run);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Run> listAllIlluminaRuns() throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    for (Run run : backingManager.listAllIlluminaRuns()) {
      if (run.userCanRead(user)) {
        accessibles.add(run);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Run> listAllSolidRuns() throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    for (Run run : backingManager.listAllSolidRuns()) {
      if (run.userCanRead(user)) {
        accessibles.add(run);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<RunQC> listAllRunQCsByRunId(long runId) throws IOException {
    User user = getCurrentUser();
    Collection<RunQC> accessibles = new HashSet<>();
    for (RunQC runQC : backingManager.listAllRunQCsByRunId(runId)) {
      if (runQC.userCanRead(user)) {
        accessibles.add(runQC);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByRunId(long runId)
      throws IOException {
    User user = getCurrentUser();
    Collection<SequencerPartitionContainer<SequencerPoolPartition>> accessibles = new HashSet<>();
    for (SequencerPartitionContainer<SequencerPoolPartition> container : backingManager.listSequencerPartitionContainersByRunId(runId)) {
      if (container.userCanRead(user)) {
        accessibles.add(container);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException {
    User user = getCurrentUser();
    Collection<SequencerPartitionContainer<SequencerPoolPartition>> accessibles = new HashSet<>();
    for (SequencerPartitionContainer<SequencerPoolPartition> container : backingManager
        .listSequencerPartitionContainersByBarcode(barcode)) {
      if (container.userCanRead(user)) {
        accessibles.add(container);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Sample> listAllSamples() throws IOException {
    User user = getCurrentUser();
    Collection<Sample> accessibles = new HashSet<>();
    for (Sample sample : backingManager.listAllSamples()) {
      if (sample.userCanRead(user)) {
        accessibles.add(sample);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Sample> listAllSamplesWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<Sample> accessibles = new HashSet<>();
    for (Sample sample : backingManager.listAllSamplesWithLimit(limit)) {
      if (sample.userCanRead(user)) {
        accessibles.add(sample);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Sample> listAllSamplesByReceivedDate(long limit) throws IOException {
    User user = getCurrentUser();
    List<Sample> samples = new ArrayList<>(backingManager.listAllSamplesByReceivedDate(limit));

    for (int i = 0; i < samples.size(); i++) {
      if (!samples.get(i).userCanRead(user)) {
        samples.remove(i);
      }
    }
    return samples;
  }

  @Override
  public Collection<Sample> listAllSamplesBySearch(String query) throws IOException {
    User user = getCurrentUser();
    Collection<Sample> accessibles = new HashSet<>();
    for (Sample sample : backingManager.listAllSamplesBySearch(query)) {
      if (sample.userCanRead(user)) {
        accessibles.add(sample);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Sample> listAllSamplesByProjectId(long projectId) throws IOException {
    User user = getCurrentUser();
    Collection<Sample> accessibles = new HashSet<>();
    for (Sample sample : backingManager.listAllSamplesByProjectId(projectId)) {
      if (sample.userCanRead(user)) {
        accessibles.add(sample);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Sample> listAllSamplesByExperimentId(long experimentId) throws IOException {
    User user = getCurrentUser();
    Collection<Sample> accessibles = new HashSet<>();
    for (Sample sample : backingManager.listAllSamplesByExperimentId(experimentId)) {
      if (sample.userCanRead(user)) {
        accessibles.add(sample);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Sample> listSamplesByAlias(String alias) throws IOException {
    User user = getCurrentUser();
    Collection<Sample> accessibles = new HashSet<>();
    for (Sample sample : backingManager.listSamplesByAlias(alias)) {
      if (sample.userCanRead(user)) {
        accessibles.add(sample);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<SampleQC> listAllSampleQCsBySampleId(long sampleId) throws IOException {
    User user = getCurrentUser();
    Collection<SampleQC> accessibles = new HashSet<>();
    for (SampleQC sampleQc : backingManager.listAllSampleQCsBySampleId(sampleId)) {
      if (sampleQc.userCanRead(user)) {
        accessibles.add(sampleQc);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Library> listAllLibraries() throws IOException {
    User user = getCurrentUser();
    Collection<Library> accessibles = new HashSet<>();
    for (Library library : backingManager.listAllLibraries()) {
      if (library.userCanRead(user)) {
        accessibles.add(library);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Library> listAllLibrariesWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<Library> accessibles = new HashSet<>();
    for (Library library : backingManager.listAllLibrariesWithLimit(limit)) {
      if (library.userCanRead(user)) {
        accessibles.add(library);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Library> listAllLibrariesBySearch(String query) throws IOException {
    User user = getCurrentUser();
    Collection<Library> accessibles = new HashSet<>();
    for (Library library : backingManager.listAllLibrariesBySearch(query)) {
      if (library.userCanRead(user)) {
        accessibles.add(library);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Library> listAllLibrariesByProjectId(long projectId) throws IOException {
    User user = getCurrentUser();
    Collection<Library> accessibles = new HashSet<>();
    for (Library library : backingManager.listAllLibrariesByProjectId(projectId)) {
      if (library.userCanRead(user)) {
        accessibles.add(library);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Library> listAllLibrariesBySampleId(long sampleId) throws IOException {
    User user = getCurrentUser();
    Collection<Library> accessibles = new HashSet<>();
    for (Library library : backingManager.listAllLibrariesBySampleId(sampleId)) {
      if (library.userCanRead(user)) {
        accessibles.add(library);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryQC> listAllLibraryQCsByLibraryId(long libraryId) throws IOException {
    User user = getCurrentUser();
    Collection<LibraryQC> accessibles = new HashSet<>();
    for (LibraryQC libraryQc : backingManager.listAllLibraryQCsByLibraryId(libraryId)) {
      if (libraryQc.userCanRead(user)) {
        accessibles.add(libraryQc);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Dilution> listDilutionsBySearch(String query, PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    Collection<Dilution> accessibles = new HashSet<>();
    for (Dilution dilution : backingManager.listDilutionsBySearch(query, platformType)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Dilution> listAllDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    Collection<Dilution> accessibles = new HashSet<>();
    for (Dilution dilution : backingManager.listAllDilutionsByProjectAndPlatform(projectId, platformType)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutions() throws IOException {
    User user = getCurrentUser();
    Collection<LibraryDilution> accessibles = new HashSet<>();
    for (LibraryDilution dilution : backingManager.listAllLibraryDilutions()) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<LibraryDilution> accessibles = new HashSet<>();
    for (LibraryDilution dilution : backingManager.listAllLibraryDilutionsWithLimit(limit)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByLibraryId(long libraryId) throws IOException {
    User user = getCurrentUser();
    Collection<LibraryDilution> accessibles = new HashSet<>();
    for (LibraryDilution dilution : backingManager.listAllLibraryDilutionsByLibraryId(libraryId)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    Collection<LibraryDilution> accessibles = new HashSet<>();
    for (LibraryDilution dilution : backingManager.listAllLibraryDilutionsByPlatform(platformType)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException {
    User user = getCurrentUser();
    Collection<LibraryDilution> accessibles = new HashSet<>();
    for (LibraryDilution dilution : backingManager.listAllLibraryDilutionsByProjectId(projectId)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearch(String query, PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    Collection<LibraryDilution> accessibles = new HashSet<>();
    for (LibraryDilution dilution : backingManager.listAllLibraryDilutionsBySearch(query, platformType)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsBySearchOnly(String query) throws IOException {
    User user = getCurrentUser();
    Collection<LibraryDilution> accessibles = new HashSet<>();
    for (LibraryDilution dilution : backingManager.listAllLibraryDilutionsBySearchOnly(query)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectAndPlatform(long projectId, PlatformType platformType)
      throws IOException {
    User user = getCurrentUser();
    Collection<LibraryDilution> accessibles = new HashSet<>();
    for (LibraryDilution dilution : backingManager.listAllLibraryDilutionsByProjectAndPlatform(projectId, platformType)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutions() throws IOException {
    User user = getCurrentUser();
    Collection<emPCRDilution> accessibles = new HashSet<>();
    for (emPCRDilution dilution : backingManager.listAllEmPCRDilutions()) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByEmPcrId(long pcrId) throws IOException {
    User user = getCurrentUser();
    Collection<emPCRDilution> accessibles = new HashSet<>();
    for (emPCRDilution dilution : backingManager.listAllEmPCRDilutionsByEmPcrId(pcrId)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByPlatform(PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    Collection<emPCRDilution> accessibles = new HashSet<>();
    for (emPCRDilution dilution : backingManager.listAllEmPCRDilutionsByPlatform(platformType)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByProjectId(long projectId) throws IOException {
    User user = getCurrentUser();
    Collection<emPCRDilution> accessibles = new HashSet<>();
    for (emPCRDilution dilution : backingManager.listAllEmPCRDilutionsByProjectId(projectId)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsBySearch(String query, PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    Collection<emPCRDilution> accessibles = new HashSet<>();
    for (emPCRDilution dilution : backingManager.listAllEmPCRDilutionsBySearch(query, platformType)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    Collection<emPCRDilution> accessibles = new HashSet<>();
    for (emPCRDilution dilution : backingManager.listAllEmPCRDilutionsByProjectAndPlatform(projectId, platformType)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<emPCRDilution> listAllEmPCRDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    Collection<emPCRDilution> accessibles = new HashSet<>();
    for (emPCRDilution dilution : backingManager.listAllEmPCRDilutionsByPoolAndPlatform(poolId, platformType)) {
      if (dilution.userCanRead(user)) {
        accessibles.add(dilution);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<emPCR> listAllEmPCRs() throws IOException {
    User user = getCurrentUser();
    Collection<emPCR> accessibles = new HashSet<>();
    for (emPCR pcr : backingManager.listAllEmPCRs()) {
      if (pcr.userCanRead(user)) {
        accessibles.add(pcr);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<emPCR> listAllEmPCRsByDilutionId(long dilutionId) throws IOException {
    User user = getCurrentUser();
    Collection<emPCR> accessibles = new HashSet<>();
    for (emPCR pcr : backingManager.listAllEmPCRsByDilutionId(dilutionId)) {
      if (pcr.userCanRead(user)) {
        accessibles.add(pcr);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPools() throws IOException {
    User user = getCurrentUser();
    ArrayList<Pool<? extends Poolable>> accessibles = new ArrayList<>();
    for (Pool<? extends Poolable> pool : backingManager.listAllPools()) {
      if (pool.userCanRead(user)) {
        accessibles.add(pool);
      }
    }
    Collections.sort(accessibles);
    return accessibles;
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatform(PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    ArrayList<Pool<? extends Poolable>> accessibles = new ArrayList<>();
    for (Pool<? extends Poolable> pool : backingManager.listAllPoolsByPlatform(platformType)) {
      if (pool.userCanRead(user)) {
        accessibles.add(pool);
      }
    }
    Collections.sort(accessibles);
    return accessibles;
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    User user = getCurrentUser();
    ArrayList<Pool<? extends Poolable>> accessibles = new ArrayList<>();
    for (Pool<? extends Poolable> pool : backingManager.listAllPoolsByPlatformAndSearch(platformType, query)) {
      if (pool.userCanRead(user)) {
        accessibles.add(pool);
      }
    }
    Collections.sort(accessibles);
    return accessibles;
  }

  @Override
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatform(PlatformType platformType) throws IOException {
    User user = getCurrentUser();
    ArrayList<Pool<? extends Poolable>> accessibles = new ArrayList<>();
    for (Pool<? extends Poolable> pool : backingManager.listReadyPoolsByPlatform(platformType)) {
      if (pool.userCanRead(user)) {
        accessibles.add(pool);
      }
    }
    Collections.sort(accessibles);
    return accessibles;
  }

  @Override
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query)
      throws IOException {
    User user = getCurrentUser();
    ArrayList<Pool<? extends Poolable>> accessibles = new ArrayList<>();
    for (Pool<? extends Poolable> pool : backingManager.listReadyPoolsByPlatformAndSearch(platformType, query)) {
      if (pool.userCanRead(user)) {
        accessibles.add(pool);
      }
    }
    Collections.sort(accessibles);
    return accessibles;
  }

  @Override
  public List<Pool<? extends Poolable>> listPoolsByLibraryId(long libraryId) throws IOException {
    User user = getCurrentUser();
    ArrayList<Pool<? extends Poolable>> accessibles = new ArrayList<>();
    for (Pool<? extends Poolable> pool : backingManager.listPoolsByLibraryId(libraryId)) {
      if (pool.userCanRead(user)) {
        accessibles.add(pool);
      }
    }
    Collections.sort(accessibles);
    return accessibles;
  }

  @Override
  public List<Pool<? extends Poolable>> listPoolsBySampleId(long sampleId) throws IOException {
    User user = getCurrentUser();
    ArrayList<Pool<? extends Poolable>> accessibles = new ArrayList<>();
    for (Pool<? extends Poolable> pool : backingManager.listPoolsBySampleId(sampleId)) {
      if (pool.userCanRead(user)) {
        accessibles.add(pool);
      }
    }
    Collections.sort(accessibles);
    return accessibles;
  }

  @Override
  public List<PoolQC> listAllPoolQCsByPoolId(long poolId) throws IOException {
    User user = getCurrentUser();
    ArrayList<PoolQC> accessibles = new ArrayList<>();
    for (PoolQC qc : backingManager.listAllPoolQCsByPoolId(poolId)) {
      if (qc.userCanRead(user)) {
        accessibles.add(qc);
      }
    }
    Collections.sort(accessibles);
    return accessibles;
  }

  @Override
  public Collection<Experiment> listAllExperiments() throws IOException {
    User user = getCurrentUser();
    Collection<Experiment> accessibles = new HashSet<>();
    for (Experiment experiment : backingManager.listAllExperiments()) {
      if (experiment.userCanRead(user)) {
        accessibles.add(experiment);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Experiment> listAllExperimentsWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<Experiment> accessibles = new HashSet<>();
    for (Experiment experiment : backingManager.listAllExperimentsWithLimit(limit)) {
      if (experiment.userCanRead(user)) {
        accessibles.add(experiment);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Experiment> listAllExperimentsBySearch(String query) throws IOException {
    User user = getCurrentUser();
    Collection<Experiment> accessibles = new HashSet<>();
    for (Experiment experiment : backingManager.listAllExperimentsBySearch(query)) {
      if (experiment.userCanRead(user)) {
        accessibles.add(experiment);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Experiment> listAllExperimentsByStudyId(long studyId) throws IOException {
    User user = getCurrentUser();
    Collection<Experiment> accessibles = new HashSet<>();
    for (Experiment experiment : backingManager.listAllExperimentsByStudyId(studyId)) {
      if (experiment.userCanRead(user)) {
        accessibles.add(experiment);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Study> listAllStudies() throws IOException {
    User user = getCurrentUser();
    Collection<Study> accessibles = new HashSet<>();
    for (Study study : backingManager.listAllStudies()) {
      if (study.userCanRead(user)) {
        accessibles.add(study);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Study> listAllStudiesWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<Study> accessibles = new HashSet<>();
    for (Study study : backingManager.listAllStudiesWithLimit(limit)) {
      if (study.userCanRead(user)) {
        accessibles.add(study);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Study> listAllStudiesBySearch(String query) throws IOException {
    User user = getCurrentUser();
    Collection<Study> accessibles = new HashSet<>();
    for (Study study : backingManager.listAllStudiesBySearch(query)) {
      if (study.userCanRead(user)) {
        accessibles.add(study);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Study> listAllStudiesByProjectId(long projectId) throws IOException {
    User user = getCurrentUser();
    Collection<Study> accessibles = new HashSet<>();
    for (Study study : backingManager.listAllStudiesByProjectId(projectId)) {
      if (study.userCanRead(user)) {
        accessibles.add(study);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Study> listAllStudiesByLibraryId(long libraryId) throws IOException {
    User user = getCurrentUser();
    Collection<Study> accessibles = new HashSet<>();
    for (Study study : backingManager.listAllStudiesByLibraryId(libraryId)) {
      if (study.userCanRead(user)) {
        accessibles.add(study);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<SequencerPoolPartition> listAllSequencerPoolPartitions() throws IOException {
    User user = getCurrentUser();
    Collection<SequencerPoolPartition> accessibles = new HashSet<>();
    for (SequencerPoolPartition partition : backingManager.listAllSequencerPoolPartitions()) {
      if (partition.userCanRead(user)) {
        accessibles.add(partition);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<? extends SequencerPoolPartition> listPartitionsBySequencerPartitionContainerId(long containerId) throws IOException {
    User user = getCurrentUser();
    Collection<SequencerPoolPartition> accessibles = new HashSet<>();
    for (SequencerPoolPartition p : backingManager.listPartitionsBySequencerPartitionContainerId(containerId)) {
      if (p.userCanRead(user)) {
        accessibles.add(p);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainers() throws IOException {
    User user = getCurrentUser();
    Collection<SequencerPartitionContainer<SequencerPoolPartition>> accessibles = new HashSet<>();
    for (SequencerPartitionContainer<SequencerPoolPartition> container : backingManager.listAllSequencerPartitionContainers()) {
      if (container.userCanRead(user)) {
        accessibles.add(container);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Submission> listAllSubmissions() throws IOException {
    User user = getCurrentUser();
    Collection<Submission> accessibles = new HashSet<>();
    for (Submission submission : backingManager.listAllSubmissions()) {
      if (submission.userCanRead(user)) {
        accessibles.add(submission);
      }
    }
    return accessibles;
  }

  @Override
  @Deprecated
  public Collection<Run> listRunsByExperimentId(Long experimentId) throws IOException {
    User user = getCurrentUser();
    Collection<Run> accessibles = new HashSet<>();
    for (Run run : backingManager.listRunsByExperimentId(experimentId)) {
      if (run.userCanRead(user)) {
        accessibles.add(run);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlates() throws IOException {
    User user = getCurrentUser();
    Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> accessibles = new HashSet<>();
    for (Plate<? extends List<? extends Plateable>, ? extends Plateable> plate : backingManager.listAllPlates()) {
      if (plate.userCanRead(user)) {
        accessibles.add(plate);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlatesByProjectId(long projectId)
      throws IOException {
    User user = getCurrentUser();
    Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> accessibles = new HashSet<>();
    for (Plate<? extends List<? extends Plateable>, ? extends Plateable> plate : backingManager.listAllPlatesByProjectId(projectId)) {
      if (plate.userCanRead(user)) {
        accessibles.add(plate);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> listAllPlatesBySearch(String str) throws IOException {
    User user = getCurrentUser();
    Collection<Plate<? extends List<? extends Plateable>, ? extends Plateable>> accessibles = new HashSet<>();
    for (Plate<? extends List<? extends Plateable>, ? extends Plateable> plate : backingManager.listAllPlatesBySearch(str)) {
      if (plate.userCanRead(user)) {
        accessibles.add(plate);
      }
    }
    return accessibles;
  }

  /* deletes */
  @Override
  public void deleteProject(Project project) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteProject(project);
    }
  }

  @Override
  public void deleteStudy(Study study) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteStudy(study);
    }
  }

  @Override
  public void deleteExperiment(Experiment experiment) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteExperiment(experiment);
    }
  }

  @Override
  public void deleteSample(Sample sample) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteSample(sample);
    }
  }

  @Override
  public void deleteLibrary(Library library) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteLibrary(library);
    }
  }

  @Override
  public void deleteEmPCR(emPCR empcr) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteEmPCR(empcr);
    }
  }

  @Override
  public void deleteRun(Run run) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteRun(run);
    }
  }

  @Override
  public void deleteRunQC(RunQC runQc) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteRunQC(runQc);
    }
  }

  @Override
  public void deleteSampleQC(SampleQC sampleQc) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteSampleQC(sampleQc);
    }
  }

  @Override
  public void deleteLibraryQC(LibraryQC libraryQc) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteLibraryQC(libraryQc);
    }
  }

  @Override
  public void deleteLibraryDilution(LibraryDilution dilution) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteLibraryDilution(dilution);
    }
  }

  @Override
  public void deleteEmPCRDilution(emPCRDilution dilution) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteEmPCRDilution(dilution);
    }
  }

  @Override
  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteSequencerReference(sequencerReference);
    }
  }

  @Override
  public void deletePool(Pool pool) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deletePool(pool);
    }
  }

  @Override
  public void deletePoolQC(PoolQC poolQc) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deletePoolQC(poolQc);
    }
  }

  @Override
  public void deletePlate(Plate plate) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deletePlate(plate);
    }
  }

  @Override
  public void deleteEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
    backingManager.deleteEntityGroup(entityGroup);
  }

  @Override
  public void deleteContainer(SequencerPartitionContainer container) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deleteContainer(container);
    }
  }

  @Override
  public void deletePartition(SequencerPoolPartition partition) throws IOException {
    if (getCurrentUser().isAdmin()) {
      backingManager.deletePartition(partition);
    }
  }

  @Override
  public void deleteNote(Note note) throws IOException {
    if (getCurrentUser().isAdmin() || getCurrentUser().equals(note.getOwner())) {
      backingManager.deleteNote(note);
    }
  }

  @Override
  public int[] saveRuns(Collection<Run> runs) throws IOException {
    for (Run run : runs) {
      if (!writeCheck(run)) {
        throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Run");
      }
    }
    return backingManager.saveRuns(runs);
  }

  @Override
  public long saveRunNote(Run run, Note note) throws IOException {
    if (writeCheck(run)) {
      return backingManager.saveRunNote(run, note);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Run");
    }
  }

  @Override
  public long saveEmPcrDilution(emPCRDilution dilution) throws IOException {
    if (writeCheck(dilution)) {
      return backingManager.saveEmPcrDilution(dilution);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Dilution");
    }
  }

  @Override
  public long savePlatform(Platform platform) throws IOException {
    if (getCurrentUser().isAdmin()) {
      return backingManager.savePlatform(platform);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Platform");
    }
  }

  @Override
  public long saveStatus(Status status) throws IOException {
    if (getCurrentUser().isInternal()) {
      return backingManager.saveStatus(status);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Status");
    }
  }

  @Override
  public long saveSecurityProfile(SecurityProfile profile) throws IOException {
    if (getCurrentUser().isAdmin()) {
      return backingManager.saveSecurityProfile(profile);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this SecurityProfile");
    }
  }

  @Override
  public long saveSequencerReference(SequencerReference sequencerReference) throws IOException {
    if (getCurrentUser().isAdmin()) {
      return backingManager.saveSequencerReference(sequencerReference);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this SequencerReference");
    }
  }

  @Override
  public long saveKit(Kit kit) throws IOException {
    if (getCurrentUser().isInternal()) {
      return backingManager.saveKit(kit);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Kit");
    }
  }

  @Override
  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException {
    if (getCurrentUser().isInternal()) {
      return backingManager.saveKitDescriptor(kitDescriptor);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this KitDescriptor");
    }
  }

  @Override
  public <T extends List<S>, S extends Plateable> long savePlate(Plate<T, S> plate) throws IOException {
    if (writeCheck(plate)) {
      return backingManager.savePlate(plate);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Status");
    }
  }

  @Override
  public long saveAlert(Alert alert) throws IOException {
    if (getCurrentUser().isInternal()) {
      return backingManager.saveAlert(alert);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Alert");
    }
  }

  @Override
  public long saveBox(Box box) throws IOException {
    if (writeCheck(box)) {
      return backingManager.saveBox(box);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Box");
    }
  }

  @Override
  public LibraryType getLibraryTypeById(long libraryId) throws IOException {
    return backingManager.getLibraryTypeById(libraryId);
  }

  @Override
  public LibraryType getLibraryTypeByDescription(String description) throws IOException {
    return backingManager.getLibraryTypeByDescription(description);
  }

  @Override
  public LibraryType getLibraryTypeByDescriptionAndPlatform(String description, PlatformType platformType) throws IOException {
    return backingManager.getLibraryTypeByDescriptionAndPlatform(description, platformType);
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeById(long librarySelectionTypeId) throws IOException {
    return backingManager.getLibrarySelectionTypeById(librarySelectionTypeId);
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException {
    return backingManager.getLibrarySelectionTypeByName(name);
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeById(long libraryStrategyTypeId) throws IOException {
    return backingManager.getLibraryStrategyTypeById(libraryStrategyTypeId);
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException {
    return backingManager.getLibraryStrategyTypeByName(name);
  }

  @Override
  public TagBarcode getTagBarcodeById(long tagBarcodeId) throws IOException {
    return backingManager.getTagBarcodeById(tagBarcodeId);
  }

  @Override
  public Platform getPlatformById(long platformId) throws IOException {
    return backingManager.getPlatformById(platformId);
  }

  @Override
  public Status getStatusById(long statusId) throws IOException {
    return backingManager.getStatusById(statusId);
  }

  @Override
  public SequencerReference getSequencerReferenceById(long referenceId) throws IOException {
    return backingManager.getSequencerReferenceById(referenceId);
  }

  @Override
  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException {
    return backingManager.getSequencerReferenceByName(referenceName);
  }

  @Override
  public SequencerReference getSequencerReferenceByRunId(long runId) throws IOException {
    return backingManager.getSequencerReferenceByRunId(runId);
  }

  @Override
  public Kit getKitById(long kitId) throws IOException {
    Kit o = backingManager.getKitById(kitId);
    if (getCurrentUser().isInternal())
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Kit " + kitId);
  }

  @Override
  public Kit getKitByIdentificationBarcode(String barcode) throws IOException {
    Kit o = backingManager.getKitByIdentificationBarcode(barcode);
    if (getCurrentUser().isInternal())
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Kit " + barcode);
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    Kit o = backingManager.getKitByLotNumber(lotNumber);
    if (getCurrentUser().isInternal())
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Kit " + lotNumber);
  }

  @Override
  public KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException {
    return backingManager.getKitDescriptorById(kitDescriptorId);
  }

  @Override
  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException {
    return backingManager.getKitDescriptorByPartNumber(partNumber);
  }

  @Override
  public QcType getSampleQcTypeById(long qcTypeId) throws IOException {
    return backingManager.getSampleQcTypeById(qcTypeId);
  }

  @Override
  public QcType getSampleQcTypeByName(String qcName) throws IOException {
    return backingManager.getSampleQcTypeByName(qcName);
  }

  @Override
  public QcType getLibraryQcTypeById(long qcTypeId) throws IOException {
    return backingManager.getLibraryQcTypeById(qcTypeId);
  }

  @Override
  public QcType getLibraryQcTypeByName(String qcName) throws IOException {
    return backingManager.getLibraryQcTypeByName(qcName);
  }

  @Override
  public QcType getRunQcTypeById(long qcTypeId) throws IOException {
    return backingManager.getRunQcTypeById(qcTypeId);
  }

  @Override
  public QcType getRunQcTypeByName(String qcName) throws IOException {
    return backingManager.getRunQcTypeByName(qcName);
  }

  @Override
  public QcType getPoolQcTypeById(long qcTypeId) throws IOException {
    return backingManager.getPoolQcTypeById(qcTypeId);
  }

  @Override
  public QcType getPoolQcTypeByName(String qcName) throws IOException {
    return backingManager.getPoolQcTypeByName(qcName);
  }

  @Override
  public Alert getAlertById(long alertId) throws IOException {
    return backingManager.getAlertById(alertId);
  }

  @Override
  public Box getBoxById(long boxId) throws IOException {
    Box o = backingManager.getBoxById(boxId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Box " + boxId);
  }

  @Override
  public Box getBoxByBarcode(String barcode) throws IOException {
    Box o = backingManager.getBoxByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Box " + barcode);
  }

  @Override
  public Box getBoxByAlias(String alias) throws IOException {
    Box o = backingManager.getBoxByAlias(alias);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Box " + alias);
  }

  @Override
  public Collection<Box> listAllBoxes() throws IOException {
    User user = getCurrentUser();
    Collection<Box> accessibles = new HashSet<>();
    for (Box o : backingManager.listAllBoxes()) {
      if (o.userCanRead(user)) {
        accessibles.add(o);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Box> listAllBoxesWithLimit(long limit) throws IOException {
    User user = getCurrentUser();
    Collection<Box> accessibles = new HashSet<>();
    for (Box o : backingManager.listAllBoxesWithLimit(limit)) {
      if (o.userCanRead(user)) {
        accessibles.add(o);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Box> listAllBoxesBySearch(String query) throws IOException {
    User user = getCurrentUser();
    Collection<Box> accessibles = new HashSet<>();
    for (Box o : backingManager.listAllBoxesBySearch(query)) {
      if (o.userCanRead(user)) {
        accessibles.add(o);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Box> listAllBoxesByAlias(String alias) throws IOException {
    User user = getCurrentUser();
    Collection<Box> accessibles = new HashSet<>();
    for (Box o : backingManager.listAllBoxesByAlias(alias)) {
      if (o.userCanRead(user)) {
        accessibles.add(o);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<ChangeLog> listAllChanges(String type) throws IOException {
    if (getCurrentUser().isInternal()) return backingManager.listAllChanges(type);
    return null;
  }

  @Override
  public Collection<String> listAllSampleTypes() throws IOException {
    return backingManager.listAllSampleTypes();
  }

  @Override
  public Collection<LibraryType> listAllLibraryTypes() throws IOException {
    return backingManager.listAllLibraryTypes();
  }

  @Override
  public Collection<LibraryType> listLibraryTypesByPlatform(String platformType) throws IOException {
    return backingManager.listLibraryTypesByPlatform(platformType);
  }

  @Override
  public Collection<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException {
    return backingManager.listAllLibrarySelectionTypes();
  }

  @Override
  public Collection<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException {
    return backingManager.listAllLibraryStrategyTypes();
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodes() throws IOException {
    return backingManager.listAllTagBarcodes();
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodesByPlatform(String platformType) throws IOException {
    return backingManager.listAllTagBarcodesByPlatform(platformType);
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodesByStrategyName(String platformType) throws IOException {
    return backingManager.listAllTagBarcodesByStrategyName(platformType);
  }

  @Override
  public Collection<emPCR> listAllEmPCRsByProjectId(long projectId) throws IOException {
    Collection<emPCR> empcrs = backingManager.listAllEmPCRsByProjectId(projectId);
    Collection<emPCR> accessible = new ArrayList<>();
    for (emPCR empcr : empcrs) {
      if (empcr.userCanRead(getCurrentUser())) accessible.add(empcr);
    }
    return accessible;
  }

  @Override
  public Collection<Pool<? extends Poolable>> listPoolsByProjectId(long projectId) throws IOException {
    Collection<Pool<? extends Poolable>> pools = backingManager.listPoolsByProjectId(projectId);
    Collection<Pool<? extends Poolable>> accessible = new ArrayList<>();
    for (Pool<? extends Poolable> p : pools) {
      if (p.userCanRead(getCurrentUser())) accessible.add(p);
    }
    return accessible;
  }

  @Override
  public Collection<Platform> listAllPlatforms() throws IOException {
    return backingManager.listAllPlatforms();
  }

  @Override
  public Collection<Platform> listPlatformsOfType(PlatformType platformType) throws IOException {
    return backingManager.listPlatformsOfType(platformType);
  }

  @Override
  public Collection<String> listDistinctPlatformNames() throws IOException {
    return backingManager.listDistinctPlatformNames();
  }

  @Override
  public Collection<BoxUse> listAllBoxUses() throws IOException {
    return backingManager.listAllBoxUses();
  }

  @Override
  public Collection<String> listAllBoxUsesStrings() throws IOException {
    return backingManager.listAllBoxUsesStrings();
  }

  @Override
  public Collection<BoxSize> listAllBoxSizes() throws IOException {
    return backingManager.listAllBoxSizes();
  }

  @Override
  public Collection<String> listAllStudyTypes() throws IOException {
    return backingManager.listAllStudyTypes();
  }

  @Override
  public Collection<Boxable> getBoxablesFromBarcodeList(List<String> barcodeList) throws IOException {
    return backingManager.getBoxablesFromBarcodeList(barcodeList);
  }

  @Override
  public Collection<SequencerReference> listAllSequencerReferences() throws IOException {
    return backingManager.listAllSequencerReferences();
  }

  @Override
  public Collection<SequencerReference> listSequencerReferencesByPlatformType(PlatformType platformType) throws IOException {
    return backingManager.listSequencerReferencesByPlatformType(platformType);
  }

  @Override
  public Collection<Kit> listAllKits() throws IOException {
    return backingManager.listAllKits();
  }

  @Override
  public Collection<Kit> listKitsByExperimentId(long experimentId) throws IOException {
    return backingManager.listKitsByExperimentId(experimentId);
  }

  @Override
  public Collection<Kit> listKitsByManufacturer(String manufacturer) throws IOException {
    return backingManager.listKitsByManufacturer(manufacturer);
  }

  @Override
  public Collection<Kit> listKitsByType(KitType kitType) throws IOException {
    return backingManager.listKitsByType(kitType);
  }

  @Override
  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException {
    return backingManager.listKitDescriptorsByType(kitType);
  }

  @Override
  public Collection<KitDescriptor> listAllKitDescriptors() throws IOException {
    return backingManager.listAllKitDescriptors();
  }

  @Override
  public Collection<QcType> listAllSampleQcTypes() throws IOException {
    return backingManager.listAllSampleQcTypes();
  }

  @Override
  public Collection<QcType> listAllLibraryQcTypes() throws IOException {
    return backingManager.listAllLibraryQcTypes();
  }

  @Override
  public Collection<QcType> listAllPoolQcTypes() throws IOException {
    return backingManager.listAllPoolQcTypes();
  }

  @Override
  public Collection<QcType> listAllRunQcTypes() throws IOException {
    return backingManager.listAllRunQcTypes();
  }

  @Override
  public Collection<Status> listAllStatus() throws IOException {
    return backingManager.listAllStatus();
  }

  @Override
  public Collection<Status> listAllStatusBySequencerName(String sequencerName) throws IOException {
    return backingManager.listAllStatusBySequencerName(sequencerName);
  }

  @Override
  public Collection<Alert> listUnreadAlertsByUserId(long userId) throws IOException {
    if (getCurrentUser().getUserId() == userId || getCurrentUser().isInternal())
      return backingManager.listUnreadAlertsByUserId(userId);
    else
      return null;
  }

  @Override
  public Collection<Alert> listAlertsByUserId(long userId) throws IOException {
    if (getCurrentUser().getUserId() == userId || getCurrentUser().isInternal())
      return backingManager.listAlertsByUserId(userId);
    else
      return null;
  }

  @Override
  public Collection<Alert> listAlertsByUserId(long userId, long limit) throws IOException {
    if (getCurrentUser().getUserId() == userId || getCurrentUser().isInternal())
      return backingManager.listAlertsByUserId(userId, limit);
    else
      return null;
  }

  @Override
  public void emptySingleTube(Box box, String position) throws IOException {
    if (writeCheck(box)) {
      backingManager.emptySingleTube(box, position);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot change Box " + box.getAlias());
    }

  }

  @Override
  public void emptyAllTubes(Box box) throws IOException {
    if (writeCheck(box)) {
      backingManager.emptyAllTubes(box);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot change Box " + box.getAlias());
    }
  }

  @Override
  public void deleteBox(Box box) throws IOException {
    if (writeCheck(box)) {
      backingManager.deleteBox(box);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot change Box " + box.getAlias());
    }
  }
}
