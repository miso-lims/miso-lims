package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.SequencerReference;
import uk.ac.bbsrc.tgac.miso.core.data.SequencerServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface SequencerReferenceService extends PaginatedDataSource<SequencerReference> {

  Collection<SequencerReference> listByPlatformType(PlatformType platformType) throws IOException;

  Collection<SequencerReference> list() throws IOException;

  Collection<SequencerServiceRecord> listServiceRecordsByInstrument(long instrumentId) throws IOException;

  SequencerReference get(long instrumentId) throws IOException;

  Long create(SequencerReference instrument) throws IOException;

  void update(SequencerReference instrument) throws IOException;

  SequencerReference getByName(String name) throws IOException;

  SequencerReference getByUpgradedReferenceId(long instrumentId) throws IOException;

  SequencerServiceRecord getServiceRecord(long recordId) throws IOException;

  void updateServiceRecord(SequencerServiceRecord record) throws IOException;

  long createServiceRecord(SequencerServiceRecord record) throws IOException;

  Map<String, Integer> getSequencerReferenceColumnSizes() throws IOException;

  Map<String, Integer> getServiceRecordColumnSizes() throws IOException;

  void deleteServiceRecord(long recordId) throws IOException;

  void delete(long instrumentId) throws IOException;
}
