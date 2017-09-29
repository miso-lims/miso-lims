package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;

public interface LibraryDesignService {

  Collection<LibraryDesign> list() throws IOException;

  Collection<LibraryDesign> listByClass(SampleClass sampleClass) throws IOException;

  LibraryDesign get(long libraryDesignId) throws IOException;
}
