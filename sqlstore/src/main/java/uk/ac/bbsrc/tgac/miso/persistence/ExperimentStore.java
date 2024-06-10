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

  public Collection<Experiment> listByLibrary(long id) throws IOException;

  public List<Experiment> listByRun(long runId) throws IOException;

  public long getUsage(Experiment experiment) throws IOException;

}
