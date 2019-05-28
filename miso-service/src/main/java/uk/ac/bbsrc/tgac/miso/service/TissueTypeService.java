package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Set;

import uk.ac.bbsrc.tgac.miso.core.data.TissueType;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface TissueTypeService extends DeleterService<TissueType>, SaveService<TissueType> {

  public Set<TissueType> list() throws IOException;

}
