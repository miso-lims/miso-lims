package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.SampleType;

public interface SampleTypeDao extends BulkSaveDao<SampleType> {

  SampleType getByName(String name) throws IOException;

  long getUsage(SampleType sampleType) throws IOException;

}
