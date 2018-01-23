package uk.ac.bbsrc.tgac.miso.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface InstrumentService extends PaginatedDataSource<Instrument> {

  Collection<Instrument> listByPlatformType(PlatformType platformType) throws IOException;

  Collection<Instrument> list() throws IOException;

  Collection<ServiceRecord> listServiceRecordsByInstrument(long instrumentId) throws IOException;

  Instrument get(long instrumentId) throws IOException;

  Long create(Instrument instrument) throws IOException;

  void update(Instrument instrument) throws IOException;

  Instrument getByName(String name) throws IOException;

  Instrument getByUpgradedInstrumentId(long instrumentId) throws IOException;

  ServiceRecord getServiceRecord(long recordId) throws IOException;

  void updateServiceRecord(ServiceRecord record) throws IOException;

  long createServiceRecord(ServiceRecord record) throws IOException;

  Map<String, Integer> getInstrumentColumnSizes() throws IOException;

  Map<String, Integer> getServiceRecordColumnSizes() throws IOException;

  void deleteServiceRecord(long recordId) throws IOException;

  void delete(long instrumentId) throws IOException;
}
