package uk.ac.bbsrc.tgac.miso.core.data.workflow;

import java.io.IOException;
import java.util.Collection;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.QC;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.SampleAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.type.QcType;

/**
 * Responsible for supporting all of the necessary methods a Workflow will need to execute itself.
 * Delegates all execution to appropriate services.
 */
public interface WorkflowExecutor {
  /**
   * Creates or updates a Pool
   * 
   * @param pool
   * @return the updated Pool
   * @throws IOException
   */
  Pool save(Pool pool) throws IOException;

  /**
   * Creates or updates a SequencerPartitionContainer
   * 
   * @param spc
   * @return the updated SequencerPartitionContainer
   * @throws IOException
   */
  SequencerPartitionContainer save(SequencerPartitionContainer spc) throws IOException;

  /**
   * Creates or updates a QC
   * 
   * @param qc
   * @return the updated QC
   * @throws IOException
   */
  QC save(QC qc) throws IOException;

  Collection<QcType> getQcTypeList() throws IOException;

  /**
   * Creates or updates a Sample
   * 
   * @param sample
   * @return the updated Sample
   * @throws IOException
   */
  Sample save(Sample sample) throws IOException;

  /**
   * Creates a new Aliquot from a Stock
   * 
   * @param stock
   * @return the newly created Aliquot
   */
  SampleAliquot createAliquotFromParent(Sample stock) throws IOException;
}
