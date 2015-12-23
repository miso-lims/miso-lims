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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolderStrategy;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;
import com.eaglegenomics.simlims.core.manager.SecurityManager;

import uk.ac.bbsrc.tgac.miso.core.data.Dilution;
import uk.ac.bbsrc.tgac.miso.core.data.EntityGroup;
import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;
import uk.ac.bbsrc.tgac.miso.core.data.Nameable;
import uk.ac.bbsrc.tgac.miso.core.data.Plate;
import uk.ac.bbsrc.tgac.miso.core.data.Plateable;
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
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCR;
import uk.ac.bbsrc.tgac.miso.core.data.impl.emPCRDilution;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
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
public class UserAuthMisoRequestManager extends MisoRequestManager {
  protected static final Logger log = LoggerFactory.getLogger(UserAuthMisoRequestManager.class);

  private SecurityContextHolderStrategy securityContextHolderStrategy;
  private SecurityManager securityManager;

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
      return super.saveProject(project);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Project");
    }
  }

  @Override
  public long saveProjectOverview(ProjectOverview overview) throws IOException {
    if (writeCheck(overview.getProject())) {
      return super.saveProjectOverview(overview);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to the parent Project");
    }
  }

  @Override
  public long saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    if (writeCheck(overview.getProject())) {
      return super.saveProjectOverviewNote(overview, note);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to the parent Project");
    }
  }

  @Override
  public long saveRun(Run run) throws IOException {
    if (writeCheck(run)) {
      return super.saveRun(run);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Run");
    }
  }

  @Override
  public long saveRunQC(RunQC runQC) throws IOException {
    if (writeCheck(runQC.getRun())) {
      return super.saveRunQC(runQC);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to the parent Run");
    }
  }

  @Override
  public long saveSample(Sample sample) throws IOException {
    if (writeCheck(sample)) {
      return super.saveSample(sample);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Sample");
    }
  }

  @Override
  public long saveSampleQC(SampleQC sampleQC) throws IOException {
    if (writeCheck(sampleQC.getSample())) {
      return super.saveSampleQC(sampleQC);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to the parent Sample ");
    }
  }

  @Override
  public long saveSampleNote(Sample sample, Note note) throws IOException {
    if (writeCheck(sample)) {
      return super.saveSampleNote(sample, note);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Sample");
    }
  }

  @Override
  public long saveLibrary(Library library) throws IOException {
    if (writeCheck(library)) {
      return super.saveLibrary(library);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Library");
    }
  }

  @Override
  public long saveLibraryDilution(LibraryDilution libraryDilution) throws IOException {
    if (writeCheck(libraryDilution)) {
      return super.saveLibraryDilution(libraryDilution);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this LibraryDilution");
    }
  }

  @Override
  public long saveLibraryNote(Library library, Note note) throws IOException {
    if (writeCheck(library)) {
      return super.saveLibraryNote(library, note);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Library");
    }
  }

  @Override
  public long saveLibraryQC(LibraryQC libraryQC) throws IOException {
    if (writeCheck(libraryQC.getLibrary())) {
      return super.saveLibraryQC(libraryQC);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Library");
    }
  }

  @Override
  public long savePool(Pool pool) throws IOException {
    if (writeCheck(pool)) {
      return super.savePool(pool);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Pool");
    }
  }

  @Override
  public long savePoolQC(PoolQC poolQC) throws IOException {
    if (writeCheck(poolQC.getPool())) {
      return super.savePoolQC(poolQC);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Pool");
    }
  }

  @Override
  public long saveEmPCR(emPCR pcr) throws IOException {
    if (writeCheck(pcr)) {
      return super.saveEmPCR(pcr);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this EmPCR");
    }
  }

  @Override
  public long saveEmPCRDilution(emPCRDilution dilution) throws IOException {
    if (writeCheck(dilution)) {
      return super.saveEmPCRDilution(dilution);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this EmPCRDilution");
    }
  }

  @Override
  public long saveExperiment(Experiment experiment) throws IOException {
    if (writeCheck(experiment)) {
      return super.saveExperiment(experiment);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Experiment");
    }
  }

  @Override
  public long saveStudy(Study study) throws IOException {
    if (writeCheck(study)) {
      return super.saveStudy(study);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Study");
    }
  }

  @Override
  public long saveSequencerPoolPartition(SequencerPoolPartition partition) throws IOException {
    if (writeCheck(partition)) {
      return super.saveSequencerPoolPartition(partition);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Partition");
    }
  }

  @Override
  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException {
    if (writeCheck(container)) {
      return super.saveSequencerPartitionContainer(container);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this SequencerPartitionContainer");
    }
  }

  @Override
  public long saveSubmission(Submission submission) throws IOException {
    if (writeCheck(submission)) {
      return super.saveSubmission(submission);
    } else {
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot write to this Submission");
    }
  }

  @Override
  public long saveEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
    return super.saveEntityGroup(entityGroup);
  }

  // gets
  @Override
  public SequencerPoolPartition getSequencerPoolPartitionById(long partitionId) throws IOException {
    SequencerPoolPartition o = super.getSequencerPoolPartitionById(partitionId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Partition " + partitionId);
  }

  @Override
  public Experiment getExperimentById(long experimentId) throws IOException {
    Experiment o = super.getExperimentById(experimentId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Experiment " + experimentId);
  }

  @Override
  public Pool<? extends Poolable> getPoolById(long poolId) throws IOException {
    Pool<? extends Poolable> o = super.getPoolById(poolId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Pool " + poolId);
  }

  @Override
  public Pool<? extends Poolable> getPoolByBarcode(String barcode, PlatformType platformType) throws IOException {
    Pool<? extends Poolable> o = super.getPoolByBarcode(barcode, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Pool " + o.getId());
  }

  @Override
  public Pool<? extends Poolable> getPoolByIdBarcode(String barcode) throws IOException {
    Pool<? extends Poolable> o = super.getPoolByIdBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Pool " + o.getId());
  }

  @Override
  public Pool<? extends Poolable> getPoolByBarcode(String barcode) throws IOException {
    Pool<? extends Poolable> o = super.getPoolByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Pool " + o.getId());
  }

  @Override
  public PoolQC getPoolQCById(long qcId) throws IOException {
    PoolQC o = super.getPoolQCById(qcId);
    if (readCheck(o.getPool()))
      return o;
    else
      throw new IOException(
          "User " + getCurrentUser().getFullName() + " cannot read parent Pool " + o.getPool().getId() + " for PoolQC " + qcId);
  }

  @Override
  public Library getLibraryById(long libraryId) throws IOException {
    Library o = super.getLibraryById(libraryId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Library " + libraryId);
  }

  @Override
  public Library getLibraryByBarcode(String barcode) throws IOException {
    Library o = super.getLibraryByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Library " + o.getId());
  }

  @Override
  public Library getLibraryByAlias(String alias) throws IOException {
    Library o = super.getLibraryByAlias(alias);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Library " + o.getId());
  }

  @Override
  public Dilution getDilutionByBarcode(String barcode) throws IOException {
    Dilution o = super.getDilutionByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Dilution " + o.getId());
  }

  @Override
  public Dilution getDilutionByIdAndPlatform(long dilutionid, PlatformType platformType) throws IOException {
    Dilution o = super.getDilutionByIdAndPlatform(dilutionid, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Dilution " + o.getId());
  }

  @Override
  public Dilution getDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    Dilution o = super.getDilutionByBarcodeAndPlatform(barcode, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Dilution " + o.getId());
  }

  @Override
  public LibraryDilution getLibraryDilutionById(long dilutionId) throws IOException {
    LibraryDilution o = super.getLibraryDilutionById(dilutionId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read LibraryDilution " + dilutionId);
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException {
    LibraryDilution o = super.getLibraryDilutionByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read LibraryDilution " + o.getId());
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    LibraryDilution o = super.getLibraryDilutionByBarcodeAndPlatform(barcode, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read LibraryDilution " + o.getId());
  }

  @Override
  public LibraryQC getLibraryQCById(long qcId) throws IOException {
    LibraryQC o = super.getLibraryQCById(qcId);
    if (readCheck(o.getLibrary()))
      return o;
    else
      throw new IOException(
          "User " + getCurrentUser().getFullName() + " cannot read parent Library " + o.getLibrary().getId() + " for LibraryQC " + qcId);
  }

  @Override
  public emPCR getEmPCRById(long pcrId) throws IOException {
    emPCR o = super.getEmPCRById(pcrId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read emPCR " + pcrId);
  }

  @Override
  public emPCRDilution getEmPCRDilutionById(long dilutionId) throws IOException {
    emPCRDilution o = super.getEmPCRDilutionById(dilutionId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read emPCRDilution " + dilutionId);
  }

  @Override
  public emPCRDilution getEmPCRDilutionByBarcode(String barcode) throws IOException {
    emPCRDilution o = super.getEmPCRDilutionByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read emPCRDilution " + o.getId());
  }

  @Override
  public emPCRDilution getEmPCRDilutionByBarcodeAndPlatform(String barcode, PlatformType platformType) throws IOException {
    emPCRDilution o = super.getEmPCRDilutionByBarcodeAndPlatform(barcode, platformType);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read emPCRDilution " + o.getId());
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerById(long containerId) throws IOException {
    SequencerPartitionContainer o = super.getSequencerPartitionContainerById(containerId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read SequencerPartitionContainer " + containerId);
  }

  @Override
  public Note getNoteById(long noteId) throws IOException {
    Note o = super.getNoteById(noteId);
    User user = getCurrentUser();
    if (o.getOwner().equals(user) || user.isAdmin() || (o.isInternalOnly() && user.isInternal()))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Note " + o.getNoteId());
  }

  @Override
  public Project getProjectById(long projectId) throws IOException {
    Project o = super.getProjectById(projectId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Project " + projectId);
  }

  @Override
  public Project getProjectByAlias(String projectAlias) throws IOException {
    Project o = super.getProjectByAlias(projectAlias);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Project " + projectAlias);
  }

  @Override
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    ProjectOverview o = super.getProjectOverviewById(overviewId);
    if (readCheck(o.getProject()))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read parent Project " + o.getProject().getProjectId()
          + " for ProjectOverview " + overviewId);
  }

  @Override
  public Run getRunById(long runId) throws IOException {
    Run o = super.getRunById(runId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Run " + runId);
  }

  @Override
  public Run getRunByAlias(String alias) throws IOException {
    Run o = super.getRunByAlias(alias);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Run " + o.getId());
  }

  @Override
  public RunQC getRunQCById(long runQcId) throws IOException {
    RunQC o = super.getRunQCById(runQcId);
    if (readCheck(o.getRun()))
      return o;
    else
      throw new IOException(
          "User " + getCurrentUser().getFullName() + " cannot read parent Run " + o.getRun().getId() + " for RunQC " + runQcId);
  }

  @Override
  public Sample getSampleById(long sampleId) throws IOException {
    Sample o = super.getSampleById(sampleId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Sample " + sampleId);
  }

  @Override
  public Sample getSampleByBarcode(String barcode) throws IOException {
    Sample o = super.getSampleByBarcode(barcode);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Sample " + o.getId());
  }

  @Override
  public SampleQC getSampleQCById(long sampleQcId) throws IOException {
    SampleQC o = super.getSampleQCById(sampleQcId);
    if (readCheck(o.getSample()))
      return o;
    else
      throw new IOException(
          "User " + getCurrentUser().getFullName() + " cannot read parent Run " + o.getSample().getId() + " for SampleQC " + sampleQcId);
  }

  @Override
  public Status getStatusByRunName(String runName) throws IOException {
    Run o = super.getRunByAlias(runName);
    if (readCheck(o))
      return super.getStatusByRunName(runName);
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read parent Run " + o.getId() + " for Status");
  }

  @Override
  public Study getStudyById(long studyId) throws IOException {
    Study o = super.getStudyById(studyId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Study " + studyId);
  }

  @Override
  public Submission getSubmissionById(long submissionId) throws IOException {
    Submission o = super.getSubmissionById(submissionId);
    if (readCheck(o))
      return o;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Submission " + submissionId);
  }

  @Override
  public Plate<? extends List<? extends Plateable>, ? extends Plateable> getPlateById(long plateId) throws IOException {
    Plate<? extends List<? extends Plateable>, ? extends Plateable> p = super.getPlateById(plateId);
    if (readCheck(p))
      return p;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Plate " + plateId);
  }

  @Override
  public <T extends List<S>, S extends Plateable> Plate<T, S> getPlateByBarcode(String barcode) throws IOException {
    Plate<T, S> p = super.<T, S> getPlateByBarcode(barcode);
    if (readCheck(p))
      return p;
    else
      throw new IOException("User " + getCurrentUser().getFullName() + " cannot read Plate " + p.getId());
  }

  @Override
  public EntityGroup<? extends Nameable, ? extends Nameable> getEntityGroupById(long entityGroupId) throws IOException {
    return super.getEntityGroupById(entityGroupId);
  }

  /* lists */

  @Override
  public Collection<Project> listAllProjects() throws IOException {
    User user = getCurrentUser();
    Collection<Project> accessibles = new HashSet<>();
    for (Project project : super.listAllProjects()) {
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
    for (Project project : super.listAllProjectsWithLimit(limit)) {
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
    for (Project project : super.listAllProjectsBySearch(query)) {
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
    for (ProjectOverview projectOverview : super.listAllOverviewsByProjectId(projectId)) {
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
    Collection<Run> runs = super.listAllRuns();
    if (runs != null) {
      for (Run run : super.listAllRuns()) {
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
    Collection<Run> runs = super.listAllRunsWithLimit(limit);
    if (runs != null) {
      for (Run run : super.listAllRunsWithLimit(limit)) {
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
    for (Run run : super.listAllRunsBySearch(query)) {
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
    for (Run run : super.listAllRunsByProjectId(projectId)) {
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
    for (Run run : super.listRunsByPoolId(poolId)) {
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
    for (Run run : super.listRunsBySequencerPartitionContainerId(containerId)) {
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
    for (Run run : super.listAllLS454Runs()) {
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
    for (Run run : super.listAllIlluminaRuns()) {
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
    for (Run run : super.listAllSolidRuns()) {
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
    for (RunQC runQC : super.listAllRunQCsByRunId(runId)) {
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
    for (SequencerPartitionContainer<SequencerPoolPartition> container : super.listSequencerPartitionContainersByRunId(runId)) {
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
    for (SequencerPartitionContainer<SequencerPoolPartition> container : super.listSequencerPartitionContainersByBarcode(barcode)) {
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
    for (Sample sample : super.listAllSamples()) {
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
    for (Sample sample : super.listAllSamplesWithLimit(limit)) {
      if (sample.userCanRead(user)) {
        accessibles.add(sample);
      }
    }
    return accessibles;
  }

  @Override
  public Collection<Sample> listAllSamplesByReceivedDate(long limit) throws IOException {
    User user = getCurrentUser();
    List<Sample> samples = new ArrayList<>(super.listAllSamplesByReceivedDate(limit));

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
    for (Sample sample : super.listAllSamplesBySearch(query)) {
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
    for (Sample sample : super.listAllSamplesByProjectId(projectId)) {
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
    for (Sample sample : super.listAllSamplesByExperimentId(experimentId)) {
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
    for (Sample sample : super.listSamplesByAlias(alias)) {
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
    for (SampleQC sampleQc : super.listAllSampleQCsBySampleId(sampleId)) {
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
    for (Library library : super.listAllLibraries()) {
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
    for (Library library : super.listAllLibrariesWithLimit(limit)) {
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
    for (Library library : super.listAllLibrariesBySearch(query)) {
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
    for (Library library : super.listAllLibrariesByProjectId(projectId)) {
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
    for (Library library : super.listAllLibrariesBySampleId(sampleId)) {
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
    for (LibraryQC libraryQc : super.listAllLibraryQCsByLibraryId(libraryId)) {
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
    for (Dilution dilution : super.listDilutionsBySearch(query, platformType)) {
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
    for (Dilution dilution : super.listAllDilutionsByProjectAndPlatform(projectId, platformType)) {
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
    for (LibraryDilution dilution : super.listAllLibraryDilutions()) {
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
    for (LibraryDilution dilution : super.listAllLibraryDilutionsWithLimit(limit)) {
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
    for (LibraryDilution dilution : super.listAllLibraryDilutionsByLibraryId(libraryId)) {
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
    for (LibraryDilution dilution : super.listAllLibraryDilutionsByPlatform(platformType)) {
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
    for (LibraryDilution dilution : super.listAllLibraryDilutionsByProjectId(projectId)) {
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
    for (LibraryDilution dilution : super.listAllLibraryDilutionsBySearch(query, platformType)) {
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
    for (LibraryDilution dilution : super.listAllLibraryDilutionsBySearchOnly(query)) {
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
    for (LibraryDilution dilution : super.listAllLibraryDilutionsByProjectAndPlatform(projectId, platformType)) {
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
    for (emPCRDilution dilution : super.listAllEmPCRDilutions()) {
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
    for (emPCRDilution dilution : super.listAllEmPCRDilutionsByEmPcrId(pcrId)) {
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
    for (emPCRDilution dilution : super.listAllEmPCRDilutionsByPlatform(platformType)) {
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
    for (emPCRDilution dilution : super.listAllEmPCRDilutionsByProjectId(projectId)) {
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
    for (emPCRDilution dilution : super.listAllEmPCRDilutionsBySearch(query, platformType)) {
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
    for (emPCRDilution dilution : super.listAllEmPCRDilutionsByProjectAndPlatform(projectId, platformType)) {
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
    for (emPCRDilution dilution : super.listAllEmPCRDilutionsByPoolAndPlatform(poolId, platformType)) {
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
    for (emPCR pcr : super.listAllEmPCRs()) {
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
    for (emPCR pcr : super.listAllEmPCRsByDilutionId(dilutionId)) {
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
    for (Pool<? extends Poolable> pool : super.listAllPools()) {
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
    for (Pool<? extends Poolable> pool : super.listAllPoolsByPlatform(platformType)) {
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
    for (Pool<? extends Poolable> pool : super.listAllPoolsByPlatformAndSearch(platformType, query)) {
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
    for (Pool<? extends Poolable> pool : super.listReadyPoolsByPlatform(platformType)) {
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
    for (Pool<? extends Poolable> pool : super.listReadyPoolsByPlatformAndSearch(platformType, query)) {
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
    for (Pool<? extends Poolable> pool : super.listPoolsByLibraryId(libraryId)) {
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
    for (Pool<? extends Poolable> pool : super.listPoolsBySampleId(sampleId)) {
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
    for (PoolQC qc : super.listAllPoolQCsByPoolId(poolId)) {
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
    for (Experiment experiment : super.listAllExperiments()) {
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
    for (Experiment experiment : super.listAllExperimentsWithLimit(limit)) {
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
    for (Experiment experiment : super.listAllExperimentsBySearch(query)) {
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
    for (Experiment experiment : super.listAllExperimentsByStudyId(studyId)) {
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
    for (Study study : super.listAllStudies()) {
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
    for (Study study : super.listAllStudiesWithLimit(limit)) {
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
    for (Study study : super.listAllStudiesBySearch(query)) {
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
    for (Study study : super.listAllStudiesByProjectId(projectId)) {
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
    for (Study study : super.listAllStudiesByLibraryId(libraryId)) {
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
    for (SequencerPoolPartition partition : super.listAllSequencerPoolPartitions()) {
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
    for (SequencerPoolPartition p : super.listPartitionsBySequencerPartitionContainerId(containerId)) {
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
    for (SequencerPartitionContainer<SequencerPoolPartition> container : super.listAllSequencerPartitionContainers()) {
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
    for (Submission submission : super.listAllSubmissions()) {
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
    for (Run run : super.listRunsByExperimentId(experimentId)) {
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
    for (Plate<? extends List<? extends Plateable>, ? extends Plateable> plate : super.listAllPlates()) {
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
    for (Plate<? extends List<? extends Plateable>, ? extends Plateable> plate : super.listAllPlatesByProjectId(projectId)) {
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
    for (Plate<? extends List<? extends Plateable>, ? extends Plateable> plate : super.listAllPlatesBySearch(str)) {
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
      super.deleteProject(project);
    }
  }

  @Override
  public void deleteStudy(Study study) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteStudy(study);
    }
  }

  @Override
  public void deleteExperiment(Experiment experiment) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteExperiment(experiment);
    }
  }

  @Override
  public void deleteSample(Sample sample) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteSample(sample);
    }
  }

  @Override
  public void deleteLibrary(Library library) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteLibrary(library);
    }
  }

  @Override
  public void deleteEmPCR(emPCR empcr) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteEmPCR(empcr);
    }
  }

  @Override
  public void deleteRun(Run run) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteRun(run);
    }
  }

  @Override
  public void deleteRunQC(RunQC runQc) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteRunQC(runQc);
    }
  }

  @Override
  public void deleteSampleQC(SampleQC sampleQc) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteSampleQC(sampleQc);
    }
  }

  @Override
  public void deleteLibraryQC(LibraryQC libraryQc) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteLibraryQC(libraryQc);
    }
  }

  @Override
  public void deleteLibraryDilution(LibraryDilution dilution) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteLibraryDilution(dilution);
    }
  }

  @Override
  public void deleteEmPCRDilution(emPCRDilution dilution) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteEmPCRDilution(dilution);
    }
  }

  @Override
  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteSequencerReference(sequencerReference);
    }
  }

  @Override
  public void deletePool(Pool pool) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deletePool(pool);
    }
  }

  @Override
  public void deletePoolQC(PoolQC poolQc) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deletePoolQC(poolQc);
    }
  }

  @Override
  public void deletePlate(Plate plate) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deletePlate(plate);
    }
  }

  @Override
  public void deleteEntityGroup(EntityGroup<? extends Nameable, ? extends Nameable> entityGroup) throws IOException {
    super.deleteEntityGroup(entityGroup);
  }

  @Override
  public void deleteContainer(SequencerPartitionContainer container) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deleteContainer(container);
    }
  }

  @Override
  public void deletePartition(SequencerPoolPartition partition) throws IOException {
    if (getCurrentUser().isAdmin()) {
      super.deletePartition(partition);
    }
  }

  @Override
  public void deleteNote(Note note) throws IOException {
    if (getCurrentUser().isAdmin() || getCurrentUser().equals(note.getOwner())) {
      super.deleteNote(note);
    }
  }
}
