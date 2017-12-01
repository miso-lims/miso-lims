package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Study;
import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface StudyService extends PaginatedDataSource<Study> {
  public void delete(Study study) throws IOException;

  public Study get(long studyId) throws IOException;

  public Map<String, Integer> getColumnSizes() throws IOException;

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

  public long save(Study study) throws IOException;

}
