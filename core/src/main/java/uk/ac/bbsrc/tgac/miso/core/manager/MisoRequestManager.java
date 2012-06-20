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

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.SecurityProfile;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaPool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.illumina.IlluminaStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ls454.LS454Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.solid.SolidPool;
import uk.ac.bbsrc.tgac.miso.core.data.type.*;
import uk.ac.bbsrc.tgac.miso.core.event.Alert;
import uk.ac.bbsrc.tgac.miso.core.store.*;

import java.io.IOException;
import java.util.*;

/**
 * Implementation of a RequestManager to facilitate persistence operations on MISO model objects
 *
 * @author Rob Davey
 * @since 0.0.2
 */
public class MisoRequestManager implements RequestManager {
  @Autowired
  private AlertStore alertStore;
  @Autowired
  private DilutionStore dilutionStore;
  @Autowired
  private EmPCRStore emPCRStore;
  @Autowired
  private ExperimentStore experimentStore;
  @Autowired
  private KitStore kitStore;
  @Autowired
  private LibraryStore libraryStore;
  @Autowired
  private LibraryQcStore libraryQcStore;
  @Autowired
  private NoteStore noteStore;
  @Autowired
  private PartitionStore partitionStore;
  @Autowired
  private PlateStore plateStore;
  @Autowired
  private PlatformStore platformStore;
  @Autowired
  private ProjectStore projectStore;
  @Autowired
  private PoolStore poolStore;
  @Autowired
  private RunStore runStore;
  @Autowired
  private RunQcStore runQcStore;
  @Autowired
  private SampleStore sampleStore;
  @Autowired
  private SampleQcStore sampleQcStore;
  @Autowired
  private Store<SecurityProfile> securityProfileStore;
  @Autowired
  private SequencerPartitionContainerStore sequencerPartitionContainerStore;
  @Autowired
  private SequencerReferenceStore sequencerReferenceStore;
  @Autowired
  private StatusStore statusStore;
  @Autowired
  private StudyStore studyStore;
  @Autowired
  private Store<Submission> submissionStore;

  public void setAlertStore(AlertStore alertStore) {
    this.alertStore = alertStore;
  }

  public void setDilutionStore(DilutionStore dilutionStore) {
    this.dilutionStore = dilutionStore;
  }

  public void setEmPCRStore(EmPCRStore emPCRStore) {
    this.emPCRStore = emPCRStore;
  }

  public void setExperimentStore(ExperimentStore experimentStore) {
    this.experimentStore = experimentStore;
  }

  public void setKitStore(KitStore kitStore) {
    this.kitStore = kitStore;
  }

  public void setLibraryStore(LibraryStore libraryStore) {
    this.libraryStore = libraryStore;
  }

  public void setLibraryQcStore(LibraryQcStore libraryQcStore) {
    this.libraryQcStore = libraryQcStore;
  }

  public void setNoteStore(NoteStore noteStore) {
    this.noteStore = noteStore;
  }

  public void setPartitionStore(PartitionStore partitionStore) {
    this.partitionStore = partitionStore;
  }

  public void setPlateStore(PlateStore plateStore) {
    this.plateStore = plateStore;
  }

  public void setPlatformStore(PlatformStore platformStore) {
    this.platformStore = platformStore;
  }

  public void setPoolStore(PoolStore poolStore) {
    this.poolStore = poolStore;
  }

  public void setProjectStore(ProjectStore projectStore) {
    this.projectStore = projectStore;
  }

  public void setRunStore(RunStore runStore) {
    this.runStore = runStore;
  }

  public void setRunQcStore(RunQcStore runQcStore) {
    this.runQcStore = runQcStore;
  }

  public void setSampleStore(SampleStore sampleStore) {
    this.sampleStore = sampleStore;
  }

  public void setSampleQcStore(SampleQcStore sampleQcStore) {
    this.sampleQcStore = sampleQcStore;
  }

  public void setSecurityProfileStore(Store<SecurityProfile> securityProfileStore) {
    this.securityProfileStore = securityProfileStore;
  }

  public void setSequencerPartitionContainerStore(SequencerPartitionContainerStore sequencerPartitionContainerStore) {
    this.sequencerPartitionContainerStore = sequencerPartitionContainerStore;
  }

  public void setSequencerReferenceStore(SequencerReferenceStore sequencerReferenceStore) {
    this.sequencerReferenceStore = sequencerReferenceStore;
  }
  
  public void setStatusStore(StatusStore statusStore) {
    this.statusStore = statusStore;
  }

  public void setStudyStore(StudyStore studyStore) {
    this.studyStore = studyStore;
  }

  public void setSubmissionStore(Store<Submission> submissionStore) {
    this.submissionStore = submissionStore;
  }

  @Override
  public Collection<Project> listAllProjects() throws IOException {
    if (projectStore != null) {
      return projectStore.listAll();
    }
    else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Project> listAllProjectsBySearch(String query) throws IOException {
    if (projectStore != null) {
      return projectStore.listBySearch(query);
    }
    else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<ProjectOverview> listAllOverviewsByProjectId(long projectId) throws IOException {
    if (projectStore != null) {
      return projectStore.listOverviewsByProjectId(projectId);
    }
    else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllRuns() throws IOException {
    if (runStore != null) {
      return runStore.listAll();
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllRunsBySearch(String query) throws IOException {
    if (runStore != null) {
      return runStore.listBySearch(query);
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllRunsByProjectId(long projectId) throws IOException {
    if (runStore != null) {
      return runStore.listByProjectId(projectId);
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listRunsByPoolId(long poolId) throws IOException {
    if (runStore != null) {
      return runStore.listByPoolId(poolId);
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }    
  }

  @Override
  public Collection<Run> listRunsBySequencerPartitionContainerId(long containerId) throws IOException {
    if (runStore != null) {
      return runStore.listBySequencerPartitionContainerId(containerId);
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllLS454Runs() throws IOException {
    if (runStore != null) {
      Collection<Run> accessibleRuns = new HashSet<Run>();
      for (Run run : runStore.listAll()) {
        if (run.getPlatformType() == PlatformType.LS454) {
          accessibleRuns.add(run);
        }
      }
      return accessibleRuns;
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllIlluminaRuns() throws IOException {
    if (runStore != null) {
      Collection<Run> accessibleRuns = new HashSet<Run>();
      for (Run run : runStore.listAll()) {
        if (run.getPlatformType() == PlatformType.ILLUMINA) {
          accessibleRuns.add(run);
        }
      }
      return accessibleRuns;
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Run> listAllSolidRuns() throws IOException {
    if (runStore != null) {
      Collection<Run> accessibleRuns = new HashSet<Run>();
      for (Run run : runStore.listAll()) {
        if (run.getPlatformType() == PlatformType.SOLID) {
          accessibleRuns.add(run);
        }
      }
      return accessibleRuns;
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<RunQC> listAllRunQCsByRunId(long runId) throws IOException {
    if (runQcStore != null) {
      return runQcStore.listByRunId(runId);
    }
    else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listAllSamples() throws IOException {
    if (sampleStore != null) {
      return sampleStore.listAll();
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listAllSamplesBySearch(String query) throws IOException {
    if (sampleStore != null) {
      return sampleStore.listBySearch(query);
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listAllSamplesByProjectId(long projectId) throws IOException {
    if (sampleStore != null) {
      return sampleStore.listByProjectId(projectId);
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Sample> listAllSamplesByExperimentId(long experimentId) throws IOException {
    if (sampleStore != null) {
      return sampleStore.listByExperimentId(experimentId);
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<String> listAllSampleTypes() throws IOException {
    if (sampleStore != null) {
      return sampleStore.listAllSampleTypes();
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SampleQC> listAllSampleQCsBySampleId(long sampleId) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.listBySampleId(sampleId);
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Library> listAllLibraries() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAll();
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Library> listAllLibrariesBySearch(String query) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listBySearch(query);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Library> listAllLibrariesByProjectId(long projectId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listByProjectId(projectId);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Library> listAllLibrariesBySampleId(long sampleId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listBySampleId(sampleId);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryQC> listAllLibraryQCsByLibraryId(long libraryId) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.listByLibraryId(libraryId);
    }
    else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPools() throws IOException {
    if (poolStore != null) {
      return poolStore.listAll();
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config."); 
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatform(PlatformType platformType) throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByPlatform(platformType);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listAllPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    if (poolStore != null) {
      return poolStore.listAllByPlatformAndSearch(platformType, query);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatform(PlatformType platformType) throws IOException {
    if (poolStore != null) {
      return poolStore.listReadyByPlatform(platformType);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listReadyPoolsByPlatformAndSearch(PlatformType platformType, String query) throws IOException {
    if (poolStore != null) {
      return poolStore.listReadyByPlatformAndSearch(platformType, query);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listPoolsByProjectId(long projectId) throws IOException {
    if (poolStore != null) {
      return poolStore.listByProjectId(projectId);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Pool<? extends Poolable>> listPoolsByLibraryId(long libraryId) throws IOException {
    if (poolStore != null) {
      return poolStore.listByLibraryId(libraryId);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<Pool<? extends Poolable>> listAllIlluminaPools() throws IOException {
    if (poolStore != null) {
      return poolStore.listAllIlluminaPools();
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<Pool<? extends Poolable>> listAll454Pools() throws IOException {
    if (poolStore != null) {
      return poolStore.listAll454Pools();
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<Pool<? extends Poolable>> listAllSolidPools() throws IOException {
    if (poolStore != null) {
      return poolStore.listAllSolidPools();
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<Pool<? extends Poolable>> listReadyIlluminaPools() throws IOException {
    if (poolStore != null) {
      return poolStore.listReadyIlluminaPools();
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<Pool<? extends Poolable>> listReady454Pools() throws IOException {
    if (poolStore != null) {
      return poolStore.listReady454Pools();
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public List<Pool<? extends Poolable>> listReadySolidPools() throws IOException {
    if (poolStore != null) {
      return poolStore.listReadySolidPools();
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryType> listAllLibraryTypes() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAllLibraryTypes();
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryType> listLibraryTypesByPlatform(String platformName) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listLibraryTypesByPlatform(platformName);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibrarySelectionType> listAllLibrarySelectionTypes() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAllLibrarySelectionTypes();
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryStrategyType> listAllLibraryStrategyTypes() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAllLibraryStrategyTypes();
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodes() throws IOException {
    if (libraryStore != null) {
      return libraryStore.listAllTagBarcodes();
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<TagBarcode> listAllTagBarcodesByPlatform(String platformName) throws IOException {
    if (libraryStore != null) {
      return libraryStore.listTagBarcodesByPlatform(platformName);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutions() throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllLibraryDilutions();
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByLibraryId(long libraryId) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listByLibraryId(libraryId);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByPlatform(PlatformType platformType) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllLibraryDilutionsByPlatform(platformType);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<LibraryDilution> listAllLibraryDilutionsByProjectId(long projectId) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllLibraryDilutionsByProjectId(projectId);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutions() throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllEmPcrDilutions();
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<? extends Dilution> listAllDilutionsByPlatform(PlatformType platformType) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllDilutionsByPlatform(platformType);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<? extends Dilution> listAllDilutionsByPoolAndPlatform(long poolId, PlatformType platformType) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllDilutionsByPoolAndPlatform(poolId, platformType);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCR> listAllEmPCRs() throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.listAll();
    }
    else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCR> listAllEmPCRsByDilutionId(long dilutionId) throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.listAllByDilutionId(dilutionId);
    }
    else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCR> listAllEmPCRsByProjectId(long projectId) throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.listAllByProjectId(projectId);
    }
    else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByEmPcrId(long pcrId) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllByEmPCRId(pcrId);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByPlatform(PlatformType platformType) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllEmPcrDilutionsByPlatform(platformType);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<emPCRDilution> listAllEmPcrDilutionsByProjectId(long projectId) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllEmPcrDilutionsByProjectId(projectId);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<? extends Dilution> listAllDilutionsByProjectAndPlatform(long projectId, PlatformType platformType) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.listAllDilutionsByProjectAndPlatform(projectId, platformType);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Experiment> listAllExperiments() throws IOException {
    if (experimentStore != null) {
      return experimentStore.listAll();
    }
    else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Experiment> listAllExperimentsBySearch(String query) throws IOException {
    if (experimentStore != null) {
      return experimentStore.listBySearch(query);
    }
    else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Experiment> listAllExperimentsByStudyId(long studyId) throws IOException {
    if (experimentStore != null) {
      return experimentStore.listByStudyId(studyId);
    }
    else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Study> listAllStudies() throws IOException {
    if (studyStore != null) {
      return studyStore.listAll();
    }
    else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Study> listAllStudiesBySearch(String query) throws IOException {
    if (studyStore != null) {
      return studyStore.listBySearch(query);
    }
    else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Study> listAllStudiesByProjectId(long projectId) throws IOException {
    if (studyStore != null) {
      return studyStore.listByProjectId(projectId);
    }
    else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByRunId(long runId) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listAllSequencerPartitionContainersByRunId(runId);
    }
    else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listSequencerPartitionContainersByBarcode(String barcode) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listSequencerPartitionContainersByBarcode(barcode);
    }
    else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPoolPartition> listAllSequencerPoolPartitions() throws IOException {
    if (partitionStore != null) {
      return partitionStore.listAll();
    }
    else {
      throw new IOException("No partitionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<? extends SequencerPoolPartition> listPartitionsBySequencerPartitionContainerId(long containerId) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listPartitionsByContainerId(containerId);
    }
    else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerPartitionContainer<SequencerPoolPartition>> listAllSequencerPartitionContainers() throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.listAll();
    }
    else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Platform> listAllPlatforms() throws IOException {
    if (platformStore != null) {
      return platformStore.listAll();
    }
    else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<String> listDistinctPlatformNames() throws IOException {
    if (platformStore != null) {
      return platformStore.listDistinctPlatformNames();
    }
    else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Platform> listPlatformsOfType(PlatformType platformType) throws IOException {
    if (platformStore != null) {
      Collection<Platform> platforms = new TreeSet<Platform>();
      for (Platform platform : platformStore.listAll()) {
        if (platform.getPlatformType().equals(platformType)) {
          platforms.add(platform);
        }
      }
      return platforms;
    }
    else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }
/*
  public Collection<PlatformType> listAllPlatformTypes() throws IOException {
    if (platformStore != null) {
      return ((PlatformStore) platformStore).listAllPlatformTypes();
    }
    else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }
*/

  @Override
  public Collection<String> listAllStudyTypes() throws IOException {
    if (studyStore != null) {
      return studyStore.listAllStudyTypes();
    }
    else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Submission> listAllSubmissions() throws IOException {
    if (submissionStore != null) {
      return submissionStore.listAll();
    }
    else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  @Deprecated
  public Collection<Run> listRunsByExperimentId(Long experimentId) throws IOException {
    if (runStore != null) {
      return runStore.listByExperimentId(experimentId);
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerReference> listAllSequencerReferences() throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.listAll();
    }
    else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<SequencerReference> listSequencerReferencesByPlatformType(PlatformType platformType) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.listByPlatformType(platformType);
    }
    else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Kit> listAllKits() throws IOException {
    if (kitStore != null) {
      return kitStore.listAll();
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Kit> listKitsByExperimentId(long experimentId) throws IOException {
    if (kitStore != null) {
      return kitStore.listByExperiment(experimentId);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Kit> listKitsByManufacturer(String manufacturer) throws IOException {
    if (kitStore != null) {
      return kitStore.listByManufacturer(manufacturer);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Kit> listKitsByType(KitType kitType) throws IOException {
    if (kitStore != null) {
      return kitStore.listKitsByType(kitType);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }
/*
  public Collection<KitType> listAllKitTypes() throws IOException {
    if (kitStore != null) {
      return ((KitStore) kitStore).listAllKitTypes();
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }
*/

  @Override
  public Collection<KitDescriptor> listAllKitDescriptors() throws IOException {
    if (kitStore != null) {
      return kitStore.listAllKitDescriptors();
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<KitDescriptor> listKitDescriptorsByType(KitType kitType) throws IOException {
    if (kitStore != null) {
      return kitStore.listKitDescriptorsByType(kitType);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<QcType> listAllSampleQcTypes() throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.listAllSampleQcTypes();
    }
    else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<QcType> listAllLibraryQcTypes() throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.listAllLibraryQcTypes();
    }
    else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<QcType> listAllRunQcTypes() throws IOException {
    if (runQcStore != null) {
      return runQcStore.listAllRunQcTypes();
    }
    else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Status> listAllStatus() throws IOException {
    if (statusStore != null) {
      return statusStore.listAll();
    }
    else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Status> listAllStatusBySequencerName(String sequencerName) throws IOException {
    if (statusStore != null) {
      return statusStore.listAllBySequencerName(sequencerName);
    }
    else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Plate> listAllPlates() throws IOException {
    if (plateStore != null) {
      return plateStore.listAll();
    }
    else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Alert> listUnreadAlertsByUserId(long userId) throws IOException {
    if (alertStore != null) {
      return alertStore.listUnreadByUserId(userId);
    }
    else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Alert> listAlertsByUserId(long userId) throws IOException {
    if (alertStore != null) {
      return alertStore.listByUserId(userId);
    }
    else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Collection<Alert> listAlertsByUserId(long userId, long limit) throws IOException {
    if (alertStore != null) {
      return alertStore.listByUserId(userId, limit);
    }
    else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }

  // DELETES
  @Override
  public void deleteProject(Project project) throws IOException {
    if (projectStore != null) {
      if (!projectStore.remove(project)) {
        throw new IOException("Unable to delete Project. Make sure the project has no child entitites.");
      }
    }
    else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteStudy(Study study) throws IOException {
    if (studyStore != null) {
      if (!studyStore.remove(study)) {
        throw new IOException("Unable to delete Study. Make sure the study has no child entitites.");
      }
    }
    else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteExperiment(Experiment experiment) throws IOException {
    if (experimentStore != null) {
      if (!experimentStore.remove(experiment)) {
        throw new IOException("Unable to delete Experiment. Make sure the experiment has no child entitites.");
      }
    }
    else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteSample(Sample sample) throws IOException {
    if (sampleStore != null) {
      if (!sampleStore.remove(sample)) {
        throw new IOException("Unable to delete Sample. Make sure the sample has no child entitites.");
      }
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteLibrary(Library library) throws IOException {
    if (libraryStore != null) {
      if (!libraryStore.remove(library)) {
        throw new IOException("Unable to delete Library. Make sure the library has no child entitites.");
      }
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteEmPCR(emPCR empcr) throws IOException {
    if (emPCRStore != null) {
      if (!emPCRStore.remove(empcr)) {
        throw new IOException("Unable to delete EmPCR. Make sure the EmPCR has no child entitites.");
      }
    }
    else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteRun(Run run) throws IOException {
    if (runStore != null) {
      if (!runStore.remove(run)) {
        throw new IOException("Unable to delete Run. Make sure the run has no child entitites.");
      }
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteRunQC(RunQC runQC) throws IOException {
    if (runQcStore != null) {
      if (!runQcStore.remove(runQC)) {
        throw new IOException("Unable to delete RunQC.");
      }
    }
    else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteSampleQC(SampleQC sampleQc) throws IOException {
    if (sampleQcStore != null) {
      if (!sampleQcStore.remove(sampleQc)) {
        throw new IOException("Unable to delete SampleQC.");
      }
    }
    else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteLibraryQC(LibraryQC libraryQc) throws IOException {
    if (libraryQcStore != null) {
      if (!libraryQcStore.remove(libraryQc)) {
        throw new IOException("Unable to delete LibraryQC.");
      }
    }
    else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteDilution(Dilution dilution) throws IOException {
    if (dilutionStore != null) {
      if (!dilutionStore.remove(dilution)) {
        throw new IOException("Unable to delete Dilution.");
      }
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deleteSequencerReference(SequencerReference sequencerReference) throws IOException {
    if (sequencerReferenceStore != null) {
      if (!sequencerReferenceStore.remove(sequencerReference)) {
        throw new IOException("Unable to delete SequencerReference.");
      }
    }
    else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public void deletePool(Pool pool) throws IOException {
    if (poolStore != null) {
      if (!poolStore.remove(pool)) {
        throw new IOException("Unable to delete Pool.");
      }
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

//SAVES

  @Override
  public long saveProject(Project project) throws IOException {
    if (projectStore != null) {
      return projectStore.save(project);
    }
    else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveProjectOverview(ProjectOverview overview) throws IOException {
    if (projectStore != null) {
      return projectStore.saveOverview(overview);
    }
    else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException {
    if (noteStore != null) {
      return noteStore.saveProjectOverviewNote(overview, note);
    }
    else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveRun(Run run) throws IOException {
    if (runStore != null) {
      return runStore.save(run);
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveRunQC(RunQC runQC) throws IOException {
    if (runQcStore != null) {
      return runQcStore.save(runQC);
    }
    else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveRunNote(Run run, Note note) throws IOException {
    if (noteStore != null) {
      return noteStore.saveRunNote(run, note);
    }
    else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSample(Sample sample) throws IOException {
    if (sampleStore != null) {
      return sampleStore.save(sample);
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSampleQC(SampleQC sampleQc) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.save(sampleQc);
    }
    else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSampleNote(Sample sample, Note note) throws IOException {
    if (noteStore != null) {
      return noteStore.saveSampleNote(sample, note);
    }
    else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveDilution(Dilution dilution) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.save(dilution);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveLibrary(Library library) throws IOException {
    if (libraryStore != null) {
      return libraryStore.save(library);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveLibraryDilution(LibraryDilution libraryDilution) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.save(libraryDilution);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveLibraryNote(Library library, Note note) throws IOException {
    if (noteStore != null) {
      return noteStore.saveLibraryNote(library, note);
    }
    else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveLibraryQC(LibraryQC libraryQc) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.save(libraryQc);
    }
    else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveEmPCR(emPCR pcr) throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.save(pcr);
    }
    else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveEmPCRDilution(emPCRDilution dilution) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.save(dilution);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long savePool(Pool pool) throws IOException {
    if (poolStore != null) {
      return poolStore.save(pool);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveIlluminaPool(IlluminaPool pool) throws IOException {
    if (poolStore != null) {
      return poolStore.saveIlluminaPool(pool);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveLS454Pool(LS454Pool pool) throws IOException {
    if (poolStore != null) {
      return poolStore.save454Pool(pool);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSolidPool(SolidPool pool) throws IOException {
    if (poolStore != null) {
      return poolStore.saveSolidPool(pool);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveExperiment(Experiment experiment) throws IOException {
    if (experimentStore != null) {
      return experimentStore.save(experiment);
    }
    else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveStudy(Study study) throws IOException {
    if (studyStore != null) {
      return studyStore.save(study);
    }
    else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSequencerPoolPartition(SequencerPoolPartition partition) throws IOException {
    if (partitionStore != null) {
      return partitionStore.save(partition);
    }
    else {
      throw new IOException("No partitionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.save(container);
    }
    else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long savePlatform(Platform platform) throws IOException {
    if (platformStore != null) {
      return platformStore.save(platform);
    }
    else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveStatus(Status status) throws IOException {
    if (statusStore != null) {
      return statusStore.save(status);
    }
    else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSecurityProfile(SecurityProfile profile) throws IOException {
    if (securityProfileStore != null) {
      return securityProfileStore.save(profile);
    }
    else {
      throw new IOException("No securityProfileStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSubmission(Submission submission) throws IOException {
    if (submissionStore != null) {
      return submissionStore.save(submission);
    }
    else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveSequencerReference(SequencerReference sequencerReference) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.save(sequencerReference);
    }
    else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveKit(Kit kit) throws IOException {
    if (kitStore != null) {
      return kitStore.save(kit);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveKitDescriptor(KitDescriptor kitDescriptor) throws IOException {
    if (kitStore != null) {
      return kitStore.saveKitDescriptor(kitDescriptor);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long savePlate(Plate plate) throws IOException {
    if (plateStore != null) {
      return plateStore.save(plate);
    }
    else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public long saveAlert(Alert alert) throws IOException {
    if (alertStore != null) {
      return alertStore.save(alert);
    }
    else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }

//GETS
  @Override
  public Project getProjectById(long projectId) throws IOException {
    if (projectStore != null) {
      return projectStore.get(projectId);
    }
    else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException {
    if (projectStore != null) {
      return projectStore.getProjectOverviewById(overviewId);
    }
    else {
      throw new IOException("No projectStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Run getRunById(long runId) throws IOException {
    if (runStore != null) {
      return runStore.get(runId);
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Run getRunByAlias(String alias) throws IOException {
    if (runStore != null) {
      return runStore.getByAlias(alias);
    }
    else {
      throw new IOException("No runStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public RunQC getRunQCById(long runQcId) throws IOException {
    if (runQcStore != null) {
      return runQcStore.get(runQcId);
    }
    else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Sample getSampleById(long sampleId) throws IOException {
    if (sampleStore != null) {
      return sampleStore.get(sampleId);
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Sample getSampleByBarcode(String barcode) throws IOException {
    if (sampleStore != null) {
      return sampleStore.getByBarcode(barcode);
    }
    else {
      throw new IOException("No sampleStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SampleQC getSampleQCById(long sampleQcId) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.get(sampleQcId);
    }
    else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Library getLibraryById(long libraryId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.get(libraryId);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Library getLibraryByBarcode(String barcode) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getByBarcode(barcode);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Library getLibraryByAlias(String alias) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getByAlias(alias);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Dilution getDilutionByIdAndPlatform(long dilutionId, PlatformType platformType) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.getDilutionByIdAndPlatform(dilutionId, platformType);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryDilution getLibraryDilutionById(long dilutionId) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.getLibraryDilutionById(dilutionId);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryDilution getLibraryDilutionByBarcode(String barcode) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.getLibraryDilutionByBarcode(barcode);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryQC getLibraryQCById(long libraryQcId) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.get(libraryQcId);
    }
    else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryType getLibraryTypeById(long typeId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibraryTypeById(typeId);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryType getLibraryTypeByDescription(String description) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibraryTypeByDescription(description);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeById(long typeId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibrarySelectionTypeById(typeId);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibrarySelectionType getLibrarySelectionTypeByName(String name) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibrarySelectionTypeByName(name);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeById(long typeId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibraryStrategyTypeById(typeId);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public LibraryStrategyType getLibraryStrategyTypeByName(String name) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getLibraryStrategyTypeByName(name);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public TagBarcode getTagBarcodeById(long tagBarcodeId) throws IOException {
    if (libraryStore != null) {
      return libraryStore.getTagBarcodeById(tagBarcodeId);
    }
    else {
      throw new IOException("No libraryStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public emPCR getEmPcrById(long pcrId) throws IOException {
    if (emPCRStore != null) {
      return emPCRStore.get(pcrId);
    }
    else {
      throw new IOException("No emPCRStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public emPCRDilution getEmPcrDilutionById(long dilutionId) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.getEmPCRDilutionById(dilutionId);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public emPCRDilution getEmPcrDilutionByBarcode(String barcode) throws IOException {
    if (dilutionStore != null) {
      return dilutionStore.getEmPCRDilutionByBarcode(barcode);
    }
    else {
      throw new IOException("No dilutionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool getPoolById(long poolId) throws IOException {
    if (poolStore != null) {
      return poolStore.getPoolById(poolId);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool getIlluminaPoolById(long poolId) throws IOException {
    if (poolStore != null) {
      return poolStore.getIlluminaPoolById(poolId);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool getPoolByBarcode(String barcode) throws IOException {
    if (getIlluminaPoolByBarcode(barcode) != null) {
      return getIlluminaPoolByBarcode(barcode);
    }
    if (getLS454PoolByBarcode(barcode) != null) {
      return getLS454PoolByBarcode(barcode);
    }
    if (getSolidPoolByBarcode(barcode) != null) {
      return getSolidPoolByBarcode(barcode);
    }
    return null;
  }

  @Override
  public Pool getIlluminaPoolByBarcode(String barcode) throws IOException {
    if (poolStore != null) {
      return poolStore.getIlluminaPoolByBarcode(barcode);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool getLS454PoolById(long poolId) throws IOException {
    if (poolStore != null) {
      return poolStore.get454PoolById(poolId);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool getLS454PoolByBarcode(String barcode) throws IOException {
    if (poolStore != null) {
      return poolStore.get454PoolByBarcode(barcode);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool getSolidPoolById(long poolId) throws IOException {
    if (poolStore != null) {
      return poolStore.getSolidPoolById(poolId);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Pool getSolidPoolByBarcode(String barcode) throws IOException {
    if (poolStore != null) {
      return poolStore.getSolidPoolByBarcode(barcode);
    }
    else {
      throw new IOException("No poolStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Experiment getExperimentById(long experimentId) throws IOException {
    if (experimentStore != null) {
      return experimentStore.get(experimentId);
    }
    else {
      throw new IOException("No experimentStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Study getStudyById(long studyId) throws IOException {
    if (studyStore != null) {
      return studyStore.get(studyId);
    }
    else {
      throw new IOException("No studyStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerPoolPartition getSequencerPoolPartitionById(long partitionId) throws IOException {
    if (partitionStore != null) {
      return partitionStore.get(partitionId);
    }
    else {
      throw new IOException("No partitionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerPartitionContainer<SequencerPoolPartition> getSequencerPartitionContainerById(long containerId) throws IOException {
    if (sequencerPartitionContainerStore != null) {
      return sequencerPartitionContainerStore.get(containerId);
    }
    else {
      throw new IOException("No sequencerPartitionContainerStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Platform getPlatformById(long platformId) throws IOException {
    if (platformStore != null) {
      return platformStore.get(platformId);
    }
    else {
      throw new IOException("No platformStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Status getStatusById(long statusId) throws IOException {
    if (statusStore != null) {
      return statusStore.get(statusId);
    }
    else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Status getStatusByRunName(String runName) throws IOException {
    if (statusStore != null) {
      return statusStore.getByRunName(runName);
    }
    else {
      throw new IOException("No statusStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Note getNoteById(long noteId) throws IOException {
    if (noteStore != null) {
      return noteStore.get(noteId);
    }
    else {
      throw new IOException("No noteStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Submission getSubmissionById(long submissionId) throws IOException {
    if (submissionStore != null) {
      return submissionStore.get(submissionId);
    }
    else {
      throw new IOException("No submissionStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerReference getSequencerReferenceById(long referenceId) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.get(referenceId);
    }
    else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.getByName(referenceName);
    }
    else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public SequencerReference getSequencerReferenceByRunId(long runId) throws IOException {
    if (sequencerReferenceStore != null) {
      return sequencerReferenceStore.getByRunId(runId);
    }
    else {
      throw new IOException("No sequencerReferenceStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Kit getKitById(long kitId) throws IOException {
    if (kitStore != null) {
      return kitStore.get(kitId);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Kit getKitByIdentificationBarcode(String barcode) throws IOException {
    if (kitStore != null) {
      return kitStore.getKitByIdentificationBarcode(barcode);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Kit getKitByLotNumber(String lotNumber) throws IOException {
    if (kitStore != null) {
      return kitStore.getKitByLotNumber(lotNumber);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public KitDescriptor getKitDescriptorById(long kitDescriptorId) throws IOException {
    if (kitStore != null) {
      return kitStore.getKitDescriptorById(kitDescriptorId);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public KitDescriptor getKitDescriptorByPartNumber(String partNumber) throws IOException {
    if (kitStore != null) {
      return kitStore.getKitDescriptorByPartNumber(partNumber);
    }
    else {
      throw new IOException("No kitStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getSampleQcTypeById(long qcTypeId) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.getSampleQcTypeById(qcTypeId);
    }
    else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getSampleQcTypeByName(String qcName) throws IOException {
    if (sampleQcStore != null) {
      return sampleQcStore.getSampleQcTypeByName(qcName);
    }
    else {
      throw new IOException("No sampleQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getLibraryQcTypeById(long qcTypeId) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.getLibraryQcTypeById(qcTypeId);
    }
    else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getLibraryQcTypeByName(String qcName) throws IOException {
    if (libraryQcStore != null) {
      return libraryQcStore.getLibraryQcTypeByName(qcName);
    }
    else {
      throw new IOException("No libraryQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getRunQcTypeById(long qcTypeId) throws IOException {
    if (runQcStore != null) {
      return runQcStore.getRunQcTypeById(qcTypeId);
    }
    else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public QcType getRunQcTypeByName(String qcName) throws IOException {
    if (runQcStore != null) {
      return runQcStore.getRunQcTypeByName(qcName);
    }
    else {
      throw new IOException("No runQcStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Plate getPlateById(long plateId) throws IOException {
    if (plateStore != null) {
      return plateStore.get(plateId);
    }
    else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Plate getPlateByBarcode(String barcode) throws IOException {
    if (plateStore != null) {
      return plateStore.getPlateByIdentificationBarcode(barcode);
    }
    else {
      throw new IOException("No plateStore available. Check that it has been declared in the Spring config.");
    }
  }

  @Override
  public Alert getAlertById(long alertId) throws IOException {
    if (alertStore != null) {
      return alertStore.get(alertId);
    }
    else {
      throw new IOException("No alertStore available. Check that it has been declared in the Spring config.");
    }
  }
}