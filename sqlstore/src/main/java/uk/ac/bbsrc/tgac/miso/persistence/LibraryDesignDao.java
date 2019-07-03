package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface LibraryDesignDao extends SaveDao<LibraryDesign> {
  
  public LibraryDesign getByNameAndSampleClass(String name, SampleClass sampleClass) throws IOException;

  public List<LibraryDesign> listByClass(SampleClass sampleClass) throws IOException;

  public long getUsage(LibraryDesign design) throws IOException;

}
