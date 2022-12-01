package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface LibraryDesignService extends DeleterService<LibraryDesign>, ListService<LibraryDesign>,
    BulkSaveService<LibraryDesign> {

  List<LibraryDesign> listByClass(SampleClass sampleClass) throws IOException;

}
