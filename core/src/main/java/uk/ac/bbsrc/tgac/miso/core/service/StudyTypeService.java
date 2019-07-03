package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.StudyType;

public interface StudyTypeService extends DeleterService<StudyType>, SaveService<StudyType> {

  public List<StudyType> list() throws IOException;

}
