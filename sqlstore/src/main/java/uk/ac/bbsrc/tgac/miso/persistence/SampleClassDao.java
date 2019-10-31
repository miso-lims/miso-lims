package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface SampleClassDao extends SaveDao<SampleClass> {

  public SampleClass getByAlias(String alias) throws IOException;

  public List<SampleClass> listByCategory(String sampleCategory) throws IOException;
  
  public long getUsage(SampleClass sampleClass) throws IOException;

}