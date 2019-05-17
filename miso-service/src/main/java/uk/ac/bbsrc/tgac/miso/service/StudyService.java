package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface StudyService extends PaginatedDataSource<Study>, DeleterService<Study>, SaveService<Study> {

  @Override
  public Study get(long studyId) throws IOException;

  public StudyType getType(long id);

  /**
   * Obtain a list of all the Studys the user has access to. Access is defined as either read or write access.
   */
  public Collection<Study> listByProjectId(long projectId) throws IOException;

  public Collection<Study> listBySearch(String query) throws IOException;

  /**
   * Obtain a list of all the StudyTypes
   */
  public Collection<StudyType> listTypes() throws IOException;

  public Collection<Study> listWithLimit(long limit) throws IOException;

}
