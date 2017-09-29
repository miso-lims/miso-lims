package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;

public interface ExperimentService {
  public long save(Experiment experiment) throws IOException;

  public Experiment get(long experimentId) throws IOException;

  public Collection<Experiment> listAll() throws IOException;

  public Collection<Experiment> listAllWithLimit(long limit) throws IOException;

  public Collection<Experiment> listAllBySearch(String query) throws IOException;

  /**
   * Obtain a list of all the Experiments the user has access to. Access is defined as either read or write access.
   */
  public Collection<Experiment> listAllByStudyId(long studyId) throws IOException;

  public void delete(Experiment experiment) throws IOException;

  public Map<String, Integer> getColumnSizes() throws IOException;

}
