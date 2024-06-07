package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;

/**
 * Defines a DAO interface for storing Experiments
 * 
 * @author Rob Davey
 * @since 0.0.2
 */
public interface ExperimentStore extends SaveDao<Experiment> {

  /**
   * List all Experiments that are part of a Study given a Study ID
   * 
   * @param studyId of type long
   * @return Collection<Experiment>
   * @throws IOException when the objects cannot be retrieved
   */
  public Collection<Experiment> listByStudyId(long studyId) throws IOException;

  /**
   * List all persisted objects
   * 
   * @return Collection<Experiment>
   * @throws IOException when the objects cannot be retrieved
   */
  public Collection<Experiment> listAllWithLimit(long limit) throws IOException;

  public Collection<Experiment> listByLibrary(long id) throws IOException;

  public List<Experiment> listByRun(long runId) throws IOException;

  public long getUsage(Experiment experiment) throws IOException;

}
