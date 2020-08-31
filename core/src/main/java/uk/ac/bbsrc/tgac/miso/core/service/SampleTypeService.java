package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleType;

public interface SampleTypeService extends DeleterService<SampleType>, SaveService<SampleType> {

  public SampleType getByName(String name) throws IOException;

  public List<SampleType> list() throws IOException;

}
