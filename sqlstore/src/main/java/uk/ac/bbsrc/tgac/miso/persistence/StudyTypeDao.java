package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.StudyType;

public interface StudyTypeDao extends SaveDao<StudyType> {

  public StudyType getByName(String name) throws IOException;

  public long getUsage(StudyType type) throws IOException;

}
