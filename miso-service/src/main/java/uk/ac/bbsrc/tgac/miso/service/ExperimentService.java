package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.service.security.AuthorizationException;

public interface ExperimentService extends SaveService<Experiment> {

  public Collection<Experiment> listAll() throws IOException;

  public Collection<Experiment> listAllByLibraryId(long id) throws AuthorizationException, IOException;

  public List<Experiment> listAllByRunId(long runId) throws IOException;

  public Collection<Experiment> listAllBySearch(String query) throws IOException;

  /**
   * Obtain a list of all the Experiments the user has access to. Access is defined as either read or write access.
   */
  public Collection<Experiment> listAllByStudyId(long studyId) throws IOException;

  public Collection<Experiment> listAllWithLimit(long limit) throws IOException;

}
