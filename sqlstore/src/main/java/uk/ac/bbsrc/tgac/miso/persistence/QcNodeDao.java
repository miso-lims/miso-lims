package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;

import uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc.SampleQcNode;

public interface QcNodeDao {

  public SampleQcNode getForSample(long id) throws IOException;

  public SampleQcNode getForLibrary(long id) throws IOException;

  public SampleQcNode getForLibraryAliquot(long id) throws IOException;

  public SampleQcNode getForRunLibrary(long runId, long partitionId, long aliquotId) throws IOException;

}
