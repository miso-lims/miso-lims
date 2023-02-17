package uk.ac.bbsrc.tgac.miso.core.service;

import java.io.IOException;
import java.util.List;

import uk.ac.bbsrc.tgac.miso.core.data.Barcodable.EntityType;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.ServiceRecord;
import uk.ac.bbsrc.tgac.miso.core.data.type.InstrumentType;
import uk.ac.bbsrc.tgac.miso.core.service.exception.ValidationException;
import uk.ac.bbsrc.tgac.miso.core.util.PaginatedDataSource;

public interface InstrumentService
    extends BarcodableService<Instrument>, DeleterService<Instrument>, ListService<Instrument>,
    PaginatedDataSource<Instrument>, SaveService<Instrument> {

  @Override
  public default EntityType getEntityType() {
    return EntityType.INSTRUMENT;
  }

  List<Instrument> listByType(InstrumentType type) throws IOException;

  Instrument getByName(String name) throws IOException;

  Instrument getByUpgradedInstrumentId(long instrumentId) throws IOException;

  long addServiceRecord(ServiceRecord record, Instrument instrument) throws IOException, ValidationException;

  Instrument getByServiceRecord(ServiceRecord record) throws IOException;

}
