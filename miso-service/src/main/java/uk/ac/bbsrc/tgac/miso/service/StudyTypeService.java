package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.StudyType;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface StudyTypeService extends DeleterService<StudyType>, SaveService<StudyType> {

  public List<StudyType> list() throws IOException;

}
