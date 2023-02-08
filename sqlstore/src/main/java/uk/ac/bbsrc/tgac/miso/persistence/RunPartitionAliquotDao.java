package uk.ac.bbsrc.tgac.miso.persistence;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Partition;
import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.Run;
import uk.ac.bbsrc.tgac.miso.core.data.RunPartitionAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerPartitionContainer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;

/**
 * Data access object for working with RunPartitionAliquots.
 *
 * Note that there is not always a RunPartitionAliquot entity persisted in the database for every
 * existing Run+Partition+Aliquot relationship. Consumers do not need to know or care about this
 * distinction, however - we should always let them assume that the entity already exists for all
 * existing relationships. This requires constructing any missing entities during fetch operations.
 */
public interface RunPartitionAliquotDao {

  RunPartitionAliquot get(Run run, Partition partition, LibraryAliquot aliquot) throws IOException;

  List<RunPartitionAliquot> listByRunId(long runId) throws IOException;

  List<RunPartitionAliquot> listByAliquotId(long aliquotId) throws IOException;

  List<RunPartitionAliquot> listByLibraryIdList(Collection<Long> libraryIds) throws IOException;

  void save(RunPartitionAliquot runPartitionAliquot) throws IOException;

  void deleteForRunContainer(Run run, SequencerPartitionContainer container) throws IOException;

  void deleteForPartition(Partition partition) throws IOException;

  void deleteForPoolAliquot(Pool pool, long aliquotId) throws IOException;

}
