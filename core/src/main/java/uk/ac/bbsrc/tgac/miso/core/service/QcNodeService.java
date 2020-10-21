package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;

public interface QcNodeService {

  public SampleQcNode getForSample(long id) throws IOException;

  public SampleQcNode getForLibrary(long id) throws IOException;

  public SampleQcNode getForLibraryAliquot(long id) throws IOException;

}
