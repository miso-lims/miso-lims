package uk.ac.bbsrc.tgac.miso.core.store;

import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface LibraryDesignDao {

  /**
   * @return a list of all LibraryDesigns
   */
  List<LibraryDesign> getLibraryDesigns();
  
  List<LibraryDesign> getLibraryDesignByClass(SampleClass sampleClass);

  LibraryDesign getLibraryDesign(Long id);

}
