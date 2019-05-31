package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.StudyType;

public interface StudyTypeDao {

  public StudyType get(long id) throws IOException;

  public StudyType getByName(String name) throws IOException;

  public List<StudyType> list() throws IOException;

  public long create(StudyType type) throws IOException;

  public long update(StudyType type) throws IOException;

  public long getUsage(StudyType type) throws IOException;

}
