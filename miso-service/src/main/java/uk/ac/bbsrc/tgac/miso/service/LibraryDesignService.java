package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.service.ListService;
import uk.ac.bbsrc.tgac.miso.core.service.SaveService;

public interface LibraryDesignService extends DeleterService<LibraryDesign>, ListService<LibraryDesign>, SaveService<LibraryDesign> {

  List<LibraryDesign> listByClass(SampleClass sampleClass) throws IOException;

}
