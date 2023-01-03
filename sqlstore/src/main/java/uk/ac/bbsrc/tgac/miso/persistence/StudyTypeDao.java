package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.StudyType;

public interface StudyTypeDao extends BulkSaveDao<StudyType> {

  StudyType getByName(String name) throws IOException;

  long getUsage(StudyType type) throws IOException;

}
