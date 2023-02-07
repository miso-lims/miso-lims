package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;

public interface LibraryDesignDao extends BulkSaveDao<LibraryDesign> {
  
  LibraryDesign getByNameAndSampleClass(String name, SampleClass sampleClass) throws IOException;

  List<LibraryDesign> listByClass(SampleClass sampleClass) throws IOException;

  long getUsage(LibraryDesign design) throws IOException;

}
