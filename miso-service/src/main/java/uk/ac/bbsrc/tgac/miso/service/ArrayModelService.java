package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface ArrayModelService extends DeleterService<ArrayModel>, SaveService<ArrayModel> {

  public List<ArrayModel> list() throws IOException;

}
