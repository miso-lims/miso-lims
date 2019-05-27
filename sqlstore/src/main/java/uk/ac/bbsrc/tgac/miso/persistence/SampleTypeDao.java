package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleType;

public interface SampleTypeDao {

  SampleType get(long id) throws IOException;

  SampleType getByName(String name) throws IOException;

  List<SampleType> list() throws IOException;

  long create(SampleType sampleType) throws IOException;

  long update(SampleType sampleType) throws IOException;
  
  long getUsage(SampleType sampleType) throws IOException;

}
