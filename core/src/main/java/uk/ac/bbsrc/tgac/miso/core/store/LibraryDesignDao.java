package uk.ac.bbsrc.tgac.miso.core.store;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface LibraryDesignDao {

  /**
   * @return a list of all LibraryDesigns
   */
  List<LibraryDesign> getLibraryDesigns() throws IOException;
  
  List<LibraryDesign> getLibraryDesignByClass(SampleClass sampleClass) throws IOException;

  LibraryDesign getLibraryDesign(Long id) throws IOException;

}
