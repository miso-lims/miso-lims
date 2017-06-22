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

package uk.ac.bbsrc.tgac.miso.core.manager;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.eaglegenomics.simlims.core.Note;
import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.Box;
import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.Submission;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectOverview;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.BoxableView;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;

public interface RequestManager {

  // SAVES
  public long saveProject(Project project) throws IOException;

  public long saveProjectOverview(ProjectOverview overview) throws IOException;

  public void saveProjectOverviewNote(ProjectOverview overview, Note note) throws IOException;

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use runService.saveRuns() instead */
  @Deprecated
  public long saveRun(Run run) throws IOException;

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use runService.saveRuns() instead */
  @Deprecated
  public void saveRuns(Collection<Run> runs) throws IOException;

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use containerService.save() instead */
  @Deprecated
  public long saveSequencerPartitionContainer(SequencerPartitionContainer container) throws IOException;

  public long saveSubmission(Submission submission) throws IOException;

  public long saveBox(Box box) throws IOException;

  // GETS

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use containerService.get(containerId) instead */
  @Deprecated
  public SequencerPartitionContainer getSequencerPartitionContainerById(long containerId) throws IOException;

  public Project getProjectById(long projectId) throws IOException;

  public Project getProjectByAlias(String projectAlias) throws IOException;

  public ProjectOverview getProjectOverviewById(long overviewId) throws IOException;

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use runService.getByAlias() instead */
  @Deprecated
  public Run getRunByAlias(String alias) throws IOException;

  public Submission getSubmissionById(long submissionId) throws IOException;

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use sequencerReferenceService.getByName instead */
  @Deprecated
  public SequencerReference getSequencerReferenceByName(String referenceName) throws IOException;

  public Box getBoxById(long boxId) throws IOException;

  public Box getBoxByBarcode(String barcode) throws IOException;

  public Box getBoxByAlias(String alias) throws IOException;

  // LISTS
  /**
   * Obtain a list of all the projects the user has access to. Access is defined as either read or write access.
   */
  public Collection<Project> listAllProjects() throws IOException;

  public Collection<Project> listAllProjectsWithLimit(long limit) throws IOException;

  public Collection<Project> listAllProjectsBySearch(String query) throws IOException;

  public Collection<ProjectOverview> listAllOverviewsByProjectId(long projectId) throws IOException;

  public Collection<Box> listAllBoxes() throws IOException;

  public Collection<Box> listAllBoxesWithLimit(long limit) throws IOException;

  /** TODO: delete after refactoring NotificationConsumerMechanisms. Use containerService.listByBarcode instead */
  @Deprecated
  public Collection<SequencerPartitionContainer> listSequencerPartitionContainersByBarcode(String barcode)
      throws IOException;

  /**
   * Obtain a list of all of the Box attributes (uses, sizes)
   */
  public Collection<BoxUse> listAllBoxUses() throws IOException;

  public Collection<BoxSize> listAllBoxSizes() throws IOException;

  public Collection<Submission> listAllSubmissions() throws IOException;

  public Collection<PlatformType> listActivePlatformTypes() throws IOException;

  /**
   * Obtain a list of Boxables by supplied identificationBarcode list
   */
  public Collection<BoxableView> getBoxableViewsFromBarcodeList(Collection<String> barcodeList) throws IOException;

  public BoxableView getBoxableViewByBarcode(String barcode) throws IOException;

  public void discardSingleTube(Box box, String position) throws IOException;

  public void discardAllTubes(Box box) throws IOException;

  // DELETES

  public void deleteProjectOverviewNote(ProjectOverview projectOverview, Long noteId) throws IOException;

  public void deleteBox(Box box) throws IOException;

  public Map<String, Integer> getBoxColumnSizes() throws IOException;

  public Map<String, Integer> getProjectColumnSizes() throws IOException;

  public Map<String, Integer> getSubmissionColumnSizes() throws IOException;

  public Map<String, Integer> getUserColumnSizes() throws IOException;

  public Map<String, Integer> getGroupColumnSizes() throws IOException;

  void addProjectWatcher(Project project, User watcher) throws IOException;

  void removeProjectWatcher(Project project, User watcher) throws IOException;

}
