package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Experiment;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;

public interface ExperimentService
    extends DeleterService<Experiment>, ListService<Experiment>, SaveService<Experiment> {

  public Collection<Experiment> listAllByLibraryId(long id) throws AuthorizationException, IOException;

  public List<Experiment> listAllByRunId(long runId) throws IOException;

  /**
   * Obtain a list of all the Experiments the user has access to. Access is defined as either read or
   * write access.
   */
  public Collection<Experiment> listAllByStudyId(long studyId) throws IOException;

}
