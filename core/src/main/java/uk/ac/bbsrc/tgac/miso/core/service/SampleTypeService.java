package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.SampleType;

public interface SampleTypeService extends DeleterService<SampleType>, BulkSaveService<SampleType>,
    ListService<SampleType> {

  public SampleType getByName(String name) throws IOException;

}
