package uk.ac.bbsrc.tgac.miso.migration;

import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;

public class MigrationData {
  
  private Collection<Project> projects;
  private Collection<Sample> samples;
  private Collection<Library> libraries;
  private Collection<LibraryDilution> dilutions;
  private Collection<Pool<LibraryDilution>> pools;
  private Collection<Run> runs;

  /**
   * @return all Projects to be migrated
   */
  public Collection<Project> getProjects() {
    return projects;
  }

  public void setProjects(Collection<Project> projects) {
    this.projects = projects;
  }

  /**
   * @return all Samples to be migrated. Note: Saving these will likely require that Projects
   * have been saved first in order to generate foreign keys
   */
  public Collection<Sample> getSamples() {
    return samples;
  }

  public void setSamples(Collection<Sample> samples) {
    this.samples = samples;
  }

  /**
   * @return all Libraries to be migrated. Note: Saving these will likely require that Projects 
   * and Samples have been saved first in order to generate foreign keys
   */
  public Collection<Library> getLibraries() {
    return libraries;
  }

  public void setLibraries(Collection<Library> libraries) {
    this.libraries = libraries;
  }

  /**
   * @return all LibraryDilutions to be migrated. Note: Saving these will likely require that
   * Libraries have been saved first in order to generate foreign keys
   */
  public Collection<LibraryDilution> getDilutions() {
    return dilutions;
  }

  public void setDilutions(Collection<LibraryDilution> dilutions) {
    this.dilutions = dilutions;
  }

  /**
   * @return all Pools to be migrated. Note: Saving these will likely require that LibraryDilutions
   * have been saved first in order to generate foreign keys
   */
  public Collection<Pool<LibraryDilution>> getPools() {
    return pools;
  }

  public void setPools(Collection<Pool<LibraryDilution>> pools) {
    this.pools = pools;
  }

  /**
   * @return all Runs to be migrated, including their filled SequencerPartitionContainers. Note:
   * Saving these will likely require that Pools have been saved first in order to generate foreign
   * keys
   */
  public Collection<Run> getRuns() {
    return runs;
  }

  public void setRuns(Collection<Run> runs) {
    this.runs = runs;
  }

}
