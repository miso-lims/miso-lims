package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleType;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface SampleTypeService extends DeleterService<SampleType>, SaveService<SampleType> {

  public List<SampleType> list() throws IOException;

}
